package org.grp8.swp391.repository;

import org.grp8.swp391.entity.Notification;
import org.grp8.swp391.entity.NotificationStatus;
import org.grp8.swp391.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Long> {


    List<Notification> findByUserAndStatusOrderByCreatedTimeDesc(User user, NotificationStatus status);

    Notification findByNotificationIdAndUser(Long notificationId, User user);
}
