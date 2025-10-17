package org.grp8.swp391.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.grp8.swp391.service.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/vnpay")
public class VNPayController {

    @Autowired
    private VNPayService vnPayService;

    /**
     * Tạo URL thanh toán VNPay
     * POST /api/vnpay/create-payment
     * 
     * @param amount Số tiền (VNĐ) - sẽ tự động nhân 100 khi gửi VNPay
     * @param orderInfo Thông tin đơn hàng
     * @param orderId Mã đơn hàng (unique)
     * @param bankCode (Optional) Mã ngân hàng - nếu null, khách chọn tại VNPay
     *                 VD: NCB, VIETCOMBANK, VIETINBANK, AGRIBANK, etc.
     */
    @PostMapping("/create-payment")
    public ResponseEntity<?> createPayment(
            @RequestParam long amount,
            @RequestParam String orderInfo,
            @RequestParam String orderId,
            @RequestParam(required = false) String bankCode,
            HttpServletRequest request
    ) {
        try {
            String ipAddress = vnPayService.getIpAddress(request);
            String paymentUrl = vnPayService.createPaymentUrl(orderId, amount, orderInfo, ipAddress, bankCode);
            
            Map<String, String> response = new HashMap<>();
            response.put("paymentUrl", paymentUrl);
            response.put("orderId", orderId);
            response.put("amount", String.valueOf(amount));
            if (bankCode != null) {
                response.put("bankCode", bankCode);
            }
            
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
     */
    @GetMapping("/callback")
    public ResponseEntity<?> vnpayCallback(@RequestParam Map<String, String> params) {
        try {
            // Verify signature
            boolean isValid = vnPayService.verifyCallback(params);
            
            if (!isValid) {
                return ResponseEntity.badRequest().body(Map.of(
                    "RspCode", "97",
                    "Message", "Invalid signature"
                ));
            }

            // Lấy thông tin giao dịch
            String vnp_ResponseCode = params.get("vnp_ResponseCode");
            String vnp_TxnRef = params.get("vnp_TxnRef"); // orderId
            String vnp_Amount = params.get("vnp_Amount");
            String vnp_TransactionNo = params.get("vnp_TransactionNo");
            String vnp_PayDate = params.get("vnp_PayDate");

            // Kiểm tra kết quả thanh toán
            if ("00".equals(vnp_ResponseCode)) {
                // Thanh toán thành công
                // TODO: Cập nhật trạng thái đơn hàng trong database
                System.out.println("✅ Payment successful: " + vnp_TxnRef);
                
                return ResponseEntity.ok(Map.of(
                    "RspCode", "00",
                    "Message", "Success",
                    "orderId", vnp_TxnRef,
                    "amount", vnp_Amount,
                    "transactionNo", vnp_TransactionNo,
                    "payDate", vnp_PayDate
                ));
            } else {
                // Thanh toán thất bại
                System.out.println("❌ Payment failed: " + vnp_TxnRef + " - Code: " + vnp_ResponseCode);
                
                return ResponseEntity.ok(Map.of(
                    "RspCode", vnp_ResponseCode,
                    "Message", "Payment failed",
                    "orderId", vnp_TxnRef
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
