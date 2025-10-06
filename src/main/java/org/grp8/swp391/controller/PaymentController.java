package org.grp8.swp391.controller;

import org.grp8.swp391.dto.response.TransactionResponse;
import org.grp8.swp391.entity.Payment;
import org.grp8.swp391.entity.PaymentStatus;
import org.grp8.swp391.entity.User_Subscription;
import org.grp8.swp391.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    @Autowired
    private PaymentService  paymentService;
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
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getHistory(@PathVariable String id){
        try{
            List<TransactionResponse> pay = paymentService.getTransHistory(id);
            return ResponseEntity.ok().body(pay);
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


}


