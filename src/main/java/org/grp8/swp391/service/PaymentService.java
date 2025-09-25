package org.grp8.swp391.service;

import org.grp8.swp391.entity.Payment;
import org.grp8.swp391.entity.PaymentStatus;
import org.grp8.swp391.entity.User_Subscription;
import org.grp8.swp391.repository.PaymentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepo paymentRepo;

    public List<Payment> getAll() {
        return paymentRepo.findAll();
    }

    public List<Payment> findByStatus(PaymentStatus status) {
        return paymentRepo.findByStatus(status);
    }

    public Payment findByTransaction(String transaction) {
        return paymentRepo.findByTransactionCode(transaction);
    }

    public List<Payment> findByMethod(String method) {
        return paymentRepo.findByMethod(method);
    }

    public List<Payment> findByUSerSubscription(User_Subscription user_subscription) {
        return paymentRepo.findByUserSubscription(user_subscription);
    }





    public Payment create (Payment payment) {
        return paymentRepo.save(payment);
    }

    public void deletePaymentById(Long id) {
        paymentRepo.deleteById(id);
    }

    public Payment findPaymentById(Long id) {
        return paymentRepo.findByPaymentId(id);
    }

    public Payment updatePayment(Long id,Payment payment) {
       Payment pay = paymentRepo.findByPaymentId(id);
       if(pay==null){
           throw new RuntimeException("Payment not found");
       }

        if (payment.getAmount() != null) {
            pay.setAmount(payment.getAmount());
        }
        if (payment.getMethod() != null) {
            pay.setMethod(payment.getMethod());
        }
        if (payment.getTransactionCode() != null) {
            pay.setTransactionCode(payment.getTransactionCode());
        }
        if (payment.getStatus() != null) {
            pay.setStatus(payment.getStatus());
        }
        if (payment.getUserSubscription() != null) {
            pay.setUserSubscription(payment.getUserSubscription());
        }

        return paymentRepo.save(pay);

    }



}
