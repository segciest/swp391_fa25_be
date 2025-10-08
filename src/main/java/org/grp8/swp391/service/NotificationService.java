package org.grp8.swp391.service;

import org.grp8.swp391.entity.Notification;
import org.grp8.swp391.entity.NotificationStatus;
import org.grp8.swp391.entity.User;
import org.grp8.swp391.repository.NotificationRepo;
import org.grp8.swp391.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepo notificationRepo;

    @Autowired
    private UserRepo userRepo;


    public List<Notification> getNotificationsByUser(String userId) {
        User user = userRepo.findByUserID(userId);
        if (user == null) throw new RuntimeException("User not found");
        return notificationRepo.findByUserAndStatusOrderByCreatedTimeDesc(user, NotificationStatus.ACTIVE);
    }


    public Notification createNotification(String userId, String message) {
        User user = userRepo.findByUserID(userId);
        if (user == null) throw new RuntimeException("User not found");

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setCreatedTime(new Date());
        notification.setStatus(NotificationStatus.ACTIVE);

        return notificationRepo.save(notification);
    }

    public void hideNotification(String userId, Long notificationId) {
        User user = userRepo.findByUserID(userId);
        if (user == null) throw new RuntimeException("User not found");

        Notification notification = notificationRepo.findByNotificationIdAndUser(notificationId, user);
        if (notification == null) throw new RuntimeException("Notification not found");

        notification.setStatus(NotificationStatus.HIDDEN);
        notificationRepo.save(notification);
    }

    public void hideAllNotifications(String userId) {
        User user = userRepo.findByUserID(userId);
        if (user == null) throw new RuntimeException("User not found");

        List<Notification> list = notificationRepo.findByUserAndStatusOrderByCreatedTimeDesc(user, NotificationStatus.ACTIVE);
        for (Notification n : list) {
            n.setStatus(NotificationStatus.HIDDEN);
        }
        notificationRepo.saveAll(list);
    }

}
