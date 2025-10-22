package org.grp8.swp391.repository;

import org.grp8.swp391.entity.Payment;
import org.grp8.swp391.entity.PaymentStatus;
import org.grp8.swp391.entity.User_Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long> {
    Payment findByTransactionCode(String transactionCode);
    Payment findByOrderId(String orderId);
    
    List<Payment> findByStatus(PaymentStatus status);
    List<Payment> findByMethod(String method);

    Payment findByPaymentId(Long paymentId);

    List<Payment> findByUserSubscription(User_Subscription userSubscription);

    List<Payment> findByUserSubscription_SubscriptionId_SubId(Long subId);

    List<Payment> findByUserSubscription_User_UserID(String userId);

    // Query payments qua relationship userSubscription.user (đúng hơn là query trực tiếp userId column)
    @Query("SELECT p FROM Payment p JOIN FETCH p.userSubscription us JOIN FETCH us.user u WHERE u.userID = :userId ORDER BY p.createDate DESC")
    List<Payment> findByUserId(@Param("userId") String userId);

    @Query("SELECT p FROM Payment p JOIN FETCH p.userSubscription us JOIN FETCH us.user u WHERE u.userID = :userId AND p.status = :status ORDER BY p.createDate DESC")
    List<Payment> findByUserIdAndStatus(@Param("userId") String userId, @Param("status") PaymentStatus status);

    Long countByStatus(PaymentStatus status);
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = :status")
    Double sumAmountByStatus(@Param("status") PaymentStatus status);

}
