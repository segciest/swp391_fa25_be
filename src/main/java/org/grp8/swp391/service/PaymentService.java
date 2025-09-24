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

    public List<Payment> findAll() {
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

    public List<Payment> findUserSubBySubId(Long subId){
        return paymentRepo.findByUserSubscription_SubscriptionId_SubId(subId);
    }

    public List<Payment> findSubByUser(String id){
        return paymentRepo.findByUserSubscription_User_UserID(id);
    }


}
