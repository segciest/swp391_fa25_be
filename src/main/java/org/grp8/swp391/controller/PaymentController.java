package org.grp8.swp391.controller;

import org.grp8.swp391.dto.response.TransactionResponse;
import org.grp8.swp391.entity.Payment;
import org.grp8.swp391.entity.PaymentStatus;
import org.grp8.swp391.entity.User_Subscription;
import org.grp8.swp391.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

import java.util.List;
import org.grp8.swp391.config.JwtUtils;
import org.grp8.swp391.repository.UserRepo;
import org.grp8.swp391.entity.User;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    @Autowired
    private PaymentService  paymentService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserRepo userRepo;
    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestBody Payment payment) {
        paymentService.create(payment);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePayment(@PathVariable Long id,@RequestBody Payment payment) {
        try{
            Payment pay = paymentService.updatePayment(id, payment);
            return ResponseEntity.ok(pay);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().build();
        }

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable Long id) {
        try{
            paymentService.deletePaymentById(id);
            return ResponseEntity.ok().build();
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getPayment(@PathVariable Long id) {
        try{
            Payment pay = paymentService.findPaymentById(id);
            return ResponseEntity.ok().body(pay);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping
    public ResponseEntity<?> getAllPayment(){
        List<Payment> pay = paymentService.getAll();
        return ResponseEntity.ok().body(pay);
    }
    @GetMapping("/status")

    public ResponseEntity<?> findByStatus(@RequestParam PaymentStatus status){
        try {
            List<Payment> pay = paymentService.findByStatus(status);
            return ResponseEntity.ok().body(pay);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/transactioncode")
    public ResponseEntity<?> findByTransactionCode(@RequestParam String transactionCode){
        try {
            Payment pay = paymentService.findByTransaction(transactionCode);
            return ResponseEntity.ok().body(pay);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/method")
    public ResponseEntity<?> findByPaymentMethod(@RequestParam String paymentMethod){
        try {
            List<Payment> pay = paymentService.findByMethod(paymentMethod);
            return ResponseEntity.ok().body(pay);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/user")
    public ResponseEntity<?> findByUserSubscription(User_Subscription userSubId){
        try{
            List<Payment> pay = paymentService.findByUSerSubscription(userSubId);
            return ResponseEntity.ok().body(pay);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().build();
        }


    }
    /**
     * Lấy transaction history của user (dựa trên JWT token)
     * GET /api/payment/user/history
     */
    @GetMapping("/user/history")
    public ResponseEntity<?> getHistory(HttpServletRequest request){
        try{
            String token = jwtUtils.extractToken(request);
            if (token == null || !jwtUtils.checkValidToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized", "message", "Invalid or missing token"));
            }

            String email = jwtUtils.getUsernameFromToken(token);
            User user = userRepo.findByUserEmail(email);
            if (user == null) {
                return ResponseEntity.status(404).body(Map.of("error", "NotFound", "message", "User not found"));
            }

            String userId = user.getUserID();
            List<TransactionResponse> pay = paymentService.getTransHistory(userId);
            return ResponseEntity.ok().body(pay);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Lấy tất cả payment của user (full entity) - cho chức năng hủy/retry
     * GET /api/payment/user/{userId}/all
     */
    @GetMapping("/user/{userId}/all")
    public ResponseEntity<?> getAllPaymentsByUser(@PathVariable String userId){
        try{
            List<Payment> payments = paymentService.findPaymentsByUserId(userId);
            return ResponseEntity.ok().body(payments);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update/status/{id}")
    public ResponseEntity<?> updatePaymentStatus(@PathVariable Long id,@RequestBody PaymentStatus status){
        try{
            Payment pay = paymentService.updatePaymentStatus(id, status);
            return ResponseEntity.ok().body(pay);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Hủy payment PENDING
     * PUT /api/payment/cancel/{paymentId}
     */
    @PutMapping("/cancel/{paymentId}")
    public ResponseEntity<?> cancelPayment(@PathVariable Long paymentId) {
        try {
            Payment payment = paymentService.findPaymentById(paymentId);
            
            if (payment == null) {
                return ResponseEntity.badRequest().body("Payment not found");
            }
            
            if (payment.getStatus() != PaymentStatus.PENDING) {
                return ResponseEntity.badRequest().body("Only PENDING payment can be cancelled");
            }
            
            payment.setStatus(PaymentStatus.CANCELLED);
            payment.setUpdatedAt(new java.util.Date());
            Payment updated = paymentService.updatePayment(paymentId, payment);
            
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}




