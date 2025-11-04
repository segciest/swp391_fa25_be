package org.grp8.swp391.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.grp8.swp391.dto.request.VNPayPaymentRequest;
import org.grp8.swp391.entity.Payment;
import org.grp8.swp391.entity.PaymentStatus;
import org.grp8.swp391.entity.Subscription;
import org.grp8.swp391.entity.User;
import org.grp8.swp391.entity.User_Subscription;
import org.grp8.swp391.repository.PaymentRepo;
import org.grp8.swp391.repository.SubRepo;
import org.grp8.swp391.repository.UserRepo;
import org.grp8.swp391.repository.UserSubRepo;
import org.grp8.swp391.service.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SubRepo subRepo;

    /**
     * Tạo URL thanh toán VNPay
     * POST /api/vnpay/create-payment
     * Body: { amount, orderInfo, subscriptionId (ID của gói Sub), userId }
     * BE sẽ tự động generate orderId unique
     */
    @PostMapping("/create-payment")
    public ResponseEntity<?> createPayment(
            @RequestBody VNPayPaymentRequest paymentRequest,
            HttpServletRequest request
    ) {
        try {
            // 1. Get User và Subscription package
            User user = userRepo.findByUserID(paymentRequest.getUserId());
            if (user == null) {
                throw new RuntimeException("User not found");
            }
            
            Subscription subscription = subRepo.findById(paymentRequest.getSubscriptionId())
                    .orElseThrow(() -> new RuntimeException("Subscription package not found"));
            
            // 2. ✅ CHECK Payment PENDING (ngăn mua gói mới khi có payment chưa xử lý)
            List<Payment> pendingPayments = paymentRepo.findByUserIdAndStatus(
                paymentRequest.getUserId(), 
                PaymentStatus.PENDING
            );
            
            if (!pendingPayments.isEmpty()) {
                Payment latestPending = pendingPayments.get(0); // Lấy payment PENDING mới nhất
                long minutesAgo = (new Date().getTime() - latestPending.getCreateDate().getTime()) / 60000;
                
                if (minutesAgo < 15) {
                    // Chưa đủ 15 phút → trả về thông tin payment để user có thể hủy
                    return ResponseEntity.badRequest().body(Map.of(
                        "error", "PENDING_PAYMENT_EXISTS",
                        "message", "Bạn có giao dịch chưa hoàn tất cho gói " + 
                                   latestPending.getUserSubscription().getSubscriptionId().getSubName() + 
                                   ". Vui lòng hủy giao dịch cũ trước khi đăng ký gói mới.",
                        "pendingPayment", Map.of(
                            "paymentId", latestPending.getPaymentId(),
                            "orderId", latestPending.getOrderId(),
                            "amount", latestPending.getAmount(),
                            "packageName", latestPending.getUserSubscription().getSubscriptionId().getSubName(),
                            "createDate", latestPending.getCreateDate(),
                            "minutesRemaining", 15 - minutesAgo
                        )
                    ));
                } else {
                    // Đã > 15 phút → auto-cancel payment cũ (hết hạn VNPay)
                    latestPending.setStatus(PaymentStatus.CANCELLED);
                    latestPending.setUpdatedAt(new Date());
                    paymentRepo.save(latestPending);
                    System.out.println("⏰ Auto-cancelled expired payment: " + latestPending.getOrderId());
                }
            }
            
            // 3. ✅ HỦY TẤT CẢ gói ACTIVE cũ (để chỉ giữ 1 gói active duy nhất)
            // Logic: Khi paid hết hạn → Scheduler set EXPIRED → User phải mua gói mới
            List<User_Subscription> activeSubs = userSubRepo.findByUser(user);
            for (User_Subscription activeSub : activeSubs) {
                if ("ACTIVE".equals(activeSub.getStatus())) {
                    activeSub.setStatus("CANCELLED");
                    userSubRepo.save(activeSub);
                    System.out.println("🔄 Cancelled old subscription: " + activeSub.getSubscriptionId().getSubName());
                }
            }
            
            // 4. ✅ TẠO hoặc TÌM User_Subscription PENDING_PAYMENT
            User_Subscription userSubscription;
            
            // Tìm xem đã có User_Subscription cho gói này chưa (status PENDING_PAYMENT hoặc FAILED)
            List<User_Subscription> existingSubs = userSubRepo.findByUser(user);
            User_Subscription reusableSub = null;
            
            for (User_Subscription sub : existingSubs) {
                // Tìm subscription cùng gói, chưa ACTIVE/EXPIRED
                if (sub.getSubscriptionId().getSubId().equals(subscription.getSubId()) &&
                    ("PENDING_PAYMENT".equals(sub.getStatus()) || "FAILED".equals(sub.getStatus()))) {
                    reusableSub = sub;
                    break;
                }
            }
            
            if (reusableSub != null) {
                // ✅ REUSE subscription cũ (cho phép retry)
                userSubscription = reusableSub;
                userSubscription.setStatus("PENDING_PAYMENT"); // Reset về PENDING_PAYMENT
                userSubRepo.save(userSubscription);
                System.out.println("♻️ Reusing existing User_Subscription: " + userSubscription.getUserSubId());
            } else {
                // ✅ TẠO subscription mới
                userSubscription = new User_Subscription();
                userSubscription.setUser(user);
                userSubscription.setSubscriptionId(subscription);
                userSubscription.setStatus("PENDING_PAYMENT");
                userSubscription.setStartDate(null);
                userSubscription.setEndDate(null);
                userSubscription = userSubRepo.save(userSubscription);
                System.out.println("📝 Created new User_Subscription: " + userSubscription.getUserSubId());
            }
            
            // 5. Generate unique orderId
            String orderId = vnPayService.generateOrderId(userSubscription.getUserSubId());
            
            // 6. ✅ TẠO Payment MỚI (1 subscription có thể có nhiều payment attempt)
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
            
            // 6. Tạo payment URL (bankCode = null, user chọn tại VNPay)
            String ipAddress = vnPayService.getIpAddress(request);
            String paymentUrl = vnPayService.createPaymentUrl(
                orderId, 
                paymentRequest.getAmount(), 
                paymentRequest.getOrderInfo(), 
                ipAddress, 
                null  // bankCode = null, user chọn ngân hàng tại VNPay
            );
            
            // 8. Trả về response
            Map<String, Object> response = new HashMap<>();
            response.put("paymentUrl", paymentUrl);
            response.put("orderId", orderId);
            response.put("amount", paymentRequest.getAmount());
            response.put("paymentId", payment.getPaymentId());
            response.put("userSubId", userSubscription.getUserSubId());
            
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
                
                // 🔥 Kích hoạt User_Subscription
                User_Subscription userSub = payment.getUserSubscription();
                if (userSub != null && userSub.getSubscriptionId() != null) {
                    // ✅ HỦY TẤT CẢ gói ACTIVE cũ trước khi kích hoạt gói mới (đảm bảo chỉ 1 ACTIVE)
                    User user = userSub.getUser();
                    List<User_Subscription> oldActiveSubs = userSubRepo.findByUser(user);
                    for (User_Subscription oldSub : oldActiveSubs) {
                        if ("ACTIVE".equals(oldSub.getStatus()) && 
                            !oldSub.getUserSubId().equals(userSub.getUserSubId())) {
                            oldSub.setStatus("CANCELLED");
                            userSubRepo.save(oldSub);
                            System.out.println("🔄 [CALLBACK] Cancelled old subscription: " + oldSub.getSubscriptionId().getSubName());
                        }
                    }
                    
                    // ✅ Kích hoạt gói mới
                    userSub.setStatus("ACTIVE");
                    userSub.setStartDate(new Date());
                    
                    // Tính endDate = startDate + duration (days)
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new Date());
                    cal.add(Calendar.DAY_OF_MONTH, userSub.getSubscriptionId().getDuration());
                    userSub.setEndDate(cal.getTime());
                    
                    userSubRepo.save(userSub);
                    System.out.println("✅ Subscription activated: " + userSub.getUserSubId());
                    // Cập nhật trường subid trên bảng users để phản ánh gói hiện tại của user
                    try {
                        User u = userSub.getUser();
                        if (u != null) {
                            u.setSubid(userSub.getSubscriptionId());
                            userRepo.save(u);
                            System.out.println("🔄 Updated user's subid to: " + userSub.getSubscriptionId().getSubName());
                        }
                    } catch (Exception ex) {
                        System.out.println("⚠️ Failed to update user's subid: " + ex.getMessage());
                    }
                }
                
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
                
                // ✅ KHÔNG set User_Subscription = FAILED
                // Giữ nguyên status = "PENDING_PAYMENT" để user có thể retry
                User_Subscription userSub = payment.getUserSubscription();
                if (userSub != null) {
                    // Chỉ log, KHÔNG thay đổi status
                    System.out.println("❌ Payment failed for subscription: " + userSub.getUserSubId() + 
                                     " - User can retry payment");
                }
                
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
     * CHÚ Ý: Endpoint này CŨNG update DB (vì IPN không hoạt động với localhost)
     */
    @GetMapping("/return")
    public ResponseEntity<?> vnpayReturn(@RequestParam Map<String, String> params) {
        try {
            // 1. Verify signature
            boolean isValid = vnPayService.verifyCallback(params);
            
            if (!isValid) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Invalid signature"
                ));
            }

            // 2. Lấy thông tin giao dịch
            String vnp_ResponseCode = params.get("vnp_ResponseCode");
            String vnp_TxnRef = params.get("vnp_TxnRef");
            String vnp_Amount = params.get("vnp_Amount");
            String vnp_TransactionNo = params.get("vnp_TransactionNo");
            String vnp_BankCode = params.get("vnp_BankCode");

            // 3. Tìm Payment trong DB
            Payment payment = paymentRepo.findByOrderId(vnp_TxnRef);
            if (payment == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Order not found"
                ));
            }

            // 4. ✅ UPDATE DB (giống callback, vì localhost không nhận IPN)
            if ("00".equals(vnp_ResponseCode)) {
                // Thanh toán thành công
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setTransactionCode(vnp_TransactionNo);
                payment.setResponseCode(vnp_ResponseCode);
                payment.setBankCode(vnp_BankCode);
                payment.setUpdatedAt(new Date());
                payment.setProviderResponse(params.toString());
                paymentRepo.save(payment);
                
                // Kích hoạt User_Subscription
                User_Subscription userSub = payment.getUserSubscription();
                if (userSub != null && userSub.getSubscriptionId() != null) {
                    // ✅ HỦY TẤT CẢ gói ACTIVE cũ trước khi kích hoạt gói mới (đảm bảo chỉ 1 ACTIVE)
                    User user = userSub.getUser();
                    List<User_Subscription> oldActiveSubs = userSubRepo.findByUser(user);
                    for (User_Subscription oldSub : oldActiveSubs) {
                        if ("ACTIVE".equals(oldSub.getStatus()) && 
                            !oldSub.getUserSubId().equals(userSub.getUserSubId())) {
                            oldSub.setStatus("CANCELLED");
                            userSubRepo.save(oldSub);
                            System.out.println("🔄 [RETURN] Cancelled old subscription: " + oldSub.getSubscriptionId().getSubName());
                        }
                    }
                    
                    // ✅ Kích hoạt gói mới
                    userSub.setStatus("ACTIVE");
                    userSub.setStartDate(new Date());
                    
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new Date());
                    cal.add(Calendar.DAY_OF_MONTH, userSub.getSubscriptionId().getDuration());
                    userSub.setEndDate(cal.getTime());
                    
                    userSubRepo.save(userSub);
                    System.out.println("✅ Subscription activated via return URL: " + userSub.getUserSubId());
                        // Cập nhật trường subid trên bảng users để phản ánh gói hiện tại của user
                        try {
                            User u = userSub.getUser();
                            if (u != null) {
                                u.setSubid(userSub.getSubscriptionId());
                                userRepo.save(u);
                                System.out.println("🔄 Updated user's subid to: " + userSub.getSubscriptionId().getSubName());
                            }
                        } catch (Exception ex) {
                            System.out.println("⚠️ Failed to update user's subid (return): " + ex.getMessage());
                        }
                }
                
                System.out.println("✅ Payment successful (return): " + vnp_TxnRef);
            } else {
                // Thanh toán thất bại
                payment.setStatus(PaymentStatus.FAILED);
                payment.setResponseCode(vnp_ResponseCode);
                payment.setUpdatedAt(new Date());
                payment.setProviderResponse(params.toString());
                paymentRepo.save(payment);
                
                System.out.println("❌ Payment failed (return): " + vnp_TxnRef + " - Code: " + vnp_ResponseCode);
            }

            // 5. Trả về response cho frontend
            Map<String, Object> response = new HashMap<>();
            response.put("orderId", vnp_TxnRef);
            response.put("amount", Long.parseLong(vnp_Amount) / 100);
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

    /**
     * Retry thanh toán cho User_Subscription
     * POST /api/vnpay/retry-payment
     * Body: { userSubId, userId }
     */
    @PostMapping("/retry-payment")
    public ResponseEntity<?> retryPayment(
            @RequestBody Map<String, Object> requestBody,
            HttpServletRequest request
    ) {
        try {
            Long userSubId = Long.valueOf(requestBody.get("userSubId").toString());
            String userId = requestBody.get("userId").toString();
            
            // 1. Get User_Subscription WITH User (eager loading)
            User_Subscription userSub = userSubRepo.findByIdWithUser(userSubId)
                    .orElseThrow(() -> new RuntimeException("User_Subscription not found"));
            
            // 2. Verify ownership
            if (userSub.getUser() == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid Data",
                    "message", "User_Subscription does not have associated user"
                ));
            }
            
            if (!userSub.getUser().getUserID().equals(userId)) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Unauthorized",
                    "message", "This subscription does not belong to you. Expected: " + userSub.getUser().getUserID() + ", Got: " + userId
                ));
            }
            
            // 3. Check status
            if ("ACTIVE".equals(userSub.getStatus()) || "EXPIRED".equals(userSub.getStatus())) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Cannot retry payment",
                    "message", "Subscription is already " + userSub.getStatus()
                ));
            }
            
            // 4. Generate new orderId
            String orderId = vnPayService.generateOrderId(userSubId);
            
            // 5. Tạo Payment mới
            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setAmount(Double.valueOf(userSub.getSubscriptionId().getSubPrice()));
            payment.setOrderInfo("Thanh toán lại gói " + userSub.getSubscriptionId().getSubName());
            payment.setUserId(userId);
            payment.setUserSubscription(userSub);
            payment.setMethod("VNPAY");
            payment.setStatus(PaymentStatus.PENDING);
            payment.setCreateDate(new Date());
            paymentRepo.save(payment);
            
            // 6. Tạo payment URL
            String ipAddress = vnPayService.getIpAddress(request);
            String paymentUrl = vnPayService.createPaymentUrl(
                orderId,
                payment.getAmount().longValue(),
                payment.getOrderInfo(),
                ipAddress,
                null
            );
            
            // 7. Return response
            Map<String, Object> response = new HashMap<>();
            response.put("paymentUrl", paymentUrl);
            response.put("orderId", orderId);
            response.put("amount", payment.getAmount());
            response.put("paymentId", payment.getPaymentId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to retry payment",
                "message", e.getMessage()
            ));
        }
    }
}
