package org.grp8.swp391.controller;

import org.grp8.swp391.entity.Notification;
import org.grp8.swp391.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/{userId}")
    public List<Notification> getNotificationsByUser(@PathVariable String userId) {
        return notificationService.getNotificationsByUser(userId);
    }

    @PostMapping
    public Notification createNotification(
            @RequestParam String userId,
            @RequestParam String message
    ) {
        return notificationService.createNotification(userId, message);
    }

    @PutMapping("/{userId}/{notificationId}/hide")
    public String hideNotification(
            @PathVariable String userId,
            @PathVariable Long notificationId
    ) {
        notificationService.hideNotification(userId, notificationId);
        return "Notification hidden successfully.";
    }

    @PutMapping("/{userId}/hide-all")
    public String hideAllNotifications(@PathVariable String userId) {
        notificationService.hideAllNotifications(userId);
        return "All notifications hidden for user: " + userId;
    }


}
