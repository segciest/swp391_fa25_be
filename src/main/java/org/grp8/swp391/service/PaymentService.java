package org.grp8.swp391.service;

import org.grp8.swp391.dto.response.TransactionResponse;
import org.grp8.swp391.entity.Payment;
import org.grp8.swp391.entity.PaymentStatus;
import org.grp8.swp391.entity.User_Subscription;
import org.grp8.swp391.repository.PaymentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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


    public Payment updatePaymentStatus(Long id ,PaymentStatus status) {
        Payment pay = paymentRepo.findByPaymentId(id);
        if(pay == null){
            throw new RuntimeException("Payment not found");

        }
        return paymentRepo.save(pay);
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


    public List<TransactionResponse> getTransHistory(String userId){
        List<Payment> pay = paymentRepo.findByUserId(userId);
        List<TransactionResponse> history = new ArrayList<>();
        for(Payment p : pay){
            User_Subscription userSub = p.getUserSubscription();
            if (userSub != null) {
                history.add(new TransactionResponse(
                        userSub.getSubscriptionId().getSubName(),
                        userSub.getStartDate(),
                        userSub.getEndDate(),
                        p.getAmount(),
                        p.getMethod(),
                        p.getTransactionCode(),
                        p.getCreateDate(),
                        p.getStatus().name()));
            } else {
                // Payment without subscription (e.g., VNPay test payment)
                history.add(new TransactionResponse(
                        "No subscription",
                        null,
                        null,
                        p.getAmount(),
                        p.getMethod(),
                        p.getTransactionCode(),
                        p.getCreateDate(),
                        p.getStatus().name()));
            }
        }
        return history;
    }

    /**
     * Tạo payment mới cho VNPay
     */
    public Payment createVNPayPayment(String userId, String orderId, Double amount, String orderInfo) {
        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setOrderInfo(orderInfo);
        payment.setMethod("VNPAY");
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreateDate(new java.util.Date());
        
        return paymentRepo.save(payment);
    }

    /**
     * Cập nhật payment sau khi nhận callback từ VNPay
     */
    public Payment updateVNPayPayment(String orderId, String transactionCode, String responseCode, 
                                      String bankCode, String providerResponse) {
        Payment payment = paymentRepo.findByOrderId(orderId);
        if (payment == null) {
            throw new RuntimeException("Payment not found with orderId: " + orderId);
        }

        payment.setTransactionCode(transactionCode);
        payment.setResponseCode(responseCode);
        payment.setBankCode(bankCode);
        payment.setProviderResponse(providerResponse);
        payment.setUpdatedAt(new java.util.Date());

        // Update status based on response code
        if ("00".equals(responseCode)) {
            payment.setStatus(PaymentStatus.COMPLETED);
        } else if ("24".equals(responseCode)) {
            payment.setStatus(PaymentStatus.CANCELLED);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        return paymentRepo.save(payment);
    }

    /**
     * Tìm payment theo orderId
     */
    public Payment findByOrderId(String orderId) {
        return paymentRepo.findByOrderId(orderId);
    }
}
