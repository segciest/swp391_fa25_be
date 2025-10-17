package org.grp8.swp391.repository;

import org.grp8.swp391.entity.Subscription;
import org.grp8.swp391.entity.User;
import org.grp8.swp391.entity.User_Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSubRepo extends JpaRepository<User_Subscription, Long> {
    User_Subscription findByUserSubId(Long id);
    List<User_Subscription> findBySubscriptionId(Subscription subId);
    User_Subscription findByUserAndSubscriptionId(User user, Subscription subscription);
    List<User_Subscription> findByUser(User user);
    void deleteByUser_UserID(String userId);
    User_Subscription findFirstByUserOrderByEndDateDesc(User user);



}
