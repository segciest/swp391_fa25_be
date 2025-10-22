package org.grp8.swp391.repository;

import org.grp8.swp391.entity.Subscription;
import org.grp8.swp391.entity.User;
import org.grp8.swp391.entity.User_Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSubRepo extends JpaRepository<User_Subscription, Long> {
    User_Subscription findByUserSubId(Long id);
    List<User_Subscription> findBySubscriptionId(Subscription subId);
    User_Subscription findByUserAndSubscriptionId(User user, Subscription subscription);
    List<User_Subscription> findByUser(User user);
    void deleteByUser_UserID(String userId);
    User_Subscription findFirstByUserOrderByEndDateDesc(User user);

    // Tìm các subscription sắp hết hạn trong khoảng thời gian
    List<User_Subscription> findByEndDateBetween(Date startDate, Date endDate);

    // Tìm tất cả subscription theo status
    List<User_Subscription> findByStatus(String status);

    // Tìm subscription của user theo status (ví dụ: PENDING, ACTIVE)
    User_Subscription findByUserAndStatus(User user, String status);

    // Eager load User khi lấy User_Subscription (cho retry payment)
    @Query("SELECT us FROM User_Subscription us JOIN FETCH us.user WHERE us.userSubId = :id")
    Optional<User_Subscription> findByIdWithUser(@Param("id") Long id);

}
