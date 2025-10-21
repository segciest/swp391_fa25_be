package org.grp8.swp391.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.grp8.swp391.dto.request.VNPayPaymentRequest;
import org.grp8.swp391.entity.Payment;
import org.grp8.swp391.entity.PaymentStatus;
import org.grp8.swp391.entity.User_Subscription;
import org.grp8.swp391.repository.PaymentRepo;
import org.grp8.swp391.repository.UserSubRepo;
import org.grp8.swp391.service.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/vnpay")
public class VNPayController {

    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private UserSubRepo userSubRepo;

    /**
     * Tạo URL thanh toán VNPay
     * POST /api/vnpay/create-payment
     * Body: { amount, orderInfo, subscriptionId, userId }
     * BE sẽ tự động generate orderId unique
     */
    @PostMapping("/create-payment")
    public ResponseEntity<?> createPayment(
            @RequestBody VNPayPaymentRequest paymentRequest,
            HttpServletRequest request
    ) {
        try {
            // 1. Generate unique orderId
            String orderId = vnPayService.generateOrderId(paymentRequest.getSubscriptionId());
            
            // 2. Get User_Subscription
            User_Subscription userSubscription = userSubRepo.findById(paymentRequest.getSubscriptionId())
                    .orElseThrow(() -> new RuntimeException("Subscription not found"));
            
            // 3. Tạo Payment record trong DB với status PENDING
            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setAmount(paymentRequest.getAmount().doubleValue());
            payment.setOrderInfo(paymentRequest.getOrderInfo());
            payment.setUserId(paymentRequest.getUserId());
            payment.setUserSubscription(userSubscription);
            payment.setMethod("VNPAY");
            payment.setStatus(PaymentStatus.PENDING);
            payment.setCreateDate(new Date());
            paymentRepo.save(payment);
            
            // 4. Tạo payment URL (bankCode = null, user chọn tại VNPay)
            String ipAddress = vnPayService.getIpAddress(request);
            String paymentUrl = vnPayService.createPaymentUrl(
                orderId, 
                paymentRequest.getAmount(), 
                paymentRequest.getOrderInfo(), 
                ipAddress, 
                null  // bankCode = null, user chọn ngân hàng tại VNPay
            );
            
            // 5. Trả về response
            Map<String, Object> response = new HashMap<>();
            response.put("paymentUrl", paymentUrl);
            response.put("orderId", orderId);
            response.put("amount", paymentRequest.getAmount());
            response.put("paymentId", payment.getPaymentId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to create payment URL",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Callback từ VNPay (IPN - Instant Payment Notification)
     * GET /api/vnpay/callback
     * VNPay server gọi endpoint này để thông báo kết quả thanh toán
     */
    @GetMapping("/callback")
    public ResponseEntity<?> vnpayCallback(@RequestParam Map<String, String> params) {
        try {
            // 1. Verify signature
            boolean isValid = vnPayService.verifyCallback(params);
            
            if (!isValid) {
                System.out.println("❌ Invalid signature from VNPay");
                return ResponseEntity.badRequest().body(Map.of(
                    "RspCode", "97",
                    "Message", "Invalid signature"
                ));
            }

            // 2. Lấy thông tin giao dịch
            String vnp_ResponseCode = params.get("vnp_ResponseCode");
            String vnp_TxnRef = params.get("vnp_TxnRef"); // orderId
            String vnp_TransactionNo = params.get("vnp_TransactionNo");
            String vnp_BankCode = params.get("vnp_BankCode");

            // 3. Tìm Payment trong DB
            Payment payment = paymentRepo.findByOrderId(vnp_TxnRef);
            if (payment == null) {
                System.out.println("❌ Payment not found: " + vnp_TxnRef);
                return ResponseEntity.badRequest().body(Map.of(
                    "RspCode", "01",
                    "Message", "Order not found"
                ));
            }

            // 4. Kiểm tra kết quả thanh toán và update DB
            if ("00".equals(vnp_ResponseCode)) {
                // ✅ Thanh toán thành công
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setTransactionCode(vnp_TransactionNo);
                payment.setResponseCode(vnp_ResponseCode);
                payment.setBankCode(vnp_BankCode);
                payment.setUpdatedAt(new Date());
                payment.setProviderResponse(params.toString()); // Lưu raw response
                paymentRepo.save(payment);
                
                System.out.println("✅ Payment successful: " + vnp_TxnRef);
                
                return ResponseEntity.ok(Map.of(
                    "RspCode", "00",
                    "Message", "Success"
                ));
            } else {
                // ❌ Thanh toán thất bại
                payment.setStatus(PaymentStatus.FAILED);
                payment.setResponseCode(vnp_ResponseCode);
                payment.setUpdatedAt(new Date());
                payment.setProviderResponse(params.toString());
                paymentRepo.save(payment);
                
                System.out.println("❌ Payment failed: " + vnp_TxnRef + " - Code: " + vnp_ResponseCode);
                
                return ResponseEntity.ok(Map.of(
                    "RspCode", "00",
                    "Message", "Confirmed"
                ));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "RspCode", "99",
                "Message", "Unknown error: " + e.getMessage()
            ));
        }
    }

    /**
     * Return URL - Trang người dùng quay về sau khi thanh toán
     * GET /api/vnpay/return
     */
    @GetMapping("/return")
    public ResponseEntity<?> vnpayReturn(@RequestParam Map<String, String> params) {
        try {
            // Verify signature
            boolean isValid = vnPayService.verifyCallback(params);
            
            if (!isValid) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Invalid signature"
                ));
            }

            String vnp_ResponseCode = params.get("vnp_ResponseCode");
            String vnp_TxnRef = params.get("vnp_TxnRef");
            String vnp_Amount = params.get("vnp_Amount");
            String vnp_TransactionNo = params.get("vnp_TransactionNo");
            String vnp_BankCode = params.get("vnp_BankCode");

            Map<String, Object> response = new HashMap<>();
            response.put("orderId", vnp_TxnRef);
            response.put("amount", Long.parseLong(vnp_Amount) / 100); // Chia 100 vì VNPay nhân 100
            response.put("transactionNo", vnp_TransactionNo);
            response.put("bankCode", vnp_BankCode);
            response.put("responseCode", vnp_ResponseCode);

            if ("00".equals(vnp_ResponseCode)) {
                response.put("success", true);
                response.put("message", "Payment successful");
            } else {
                response.put("success", false);
                response.put("message", getResponseMessage(vnp_ResponseCode));
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Error processing return: " + e.getMessage()
            ));
        }
    }

    /**
     * Lấy message từ response code
     */
    private String getResponseMessage(String responseCode) {
        switch (responseCode) {
            case "00": return "Giao dịch thành công";
            case "07": return "Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường).";
            case "09": return "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng.";
            case "10": return "Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần";
            case "11": return "Giao dịch không thành công do: Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch.";
            case "12": return "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng bị khóa.";
            case "13": return "Giao dịch không thành công do Quý khách nhập sai mật khẩu xác thực giao dịch (OTP). Xin quý khách vui lòng thực hiện lại giao dịch.";
            case "24": return "Giao dịch không thành công do: Khách hàng hủy giao dịch";
            case "51": return "Giao dịch không thành công do: Tài khoản của quý khách không đủ số dư để thực hiện giao dịch.";
            case "65": return "Giao dịch không thành công do: Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày.";
            case "75": return "Ngân hàng thanh toán đang bảo trì.";
            case "79": return "Giao dịch không thành công do: KH nhập sai mật khẩu thanh toán quá số lần quy định. Xin quý khách vui lòng thực hiện lại giao dịch";
            default: return "Giao dịch thất bại";
        }
    }
}
