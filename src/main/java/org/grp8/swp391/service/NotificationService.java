package org.grp8.swp391.service;

import org.grp8.swp391.entity.*;
import org.grp8.swp391.repository.NotificationRepo;
import org.grp8.swp391.repository.UserRepo;
import org.grp8.swp391.repository.UserSubRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;
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

    // ===== CÁC METHOD MỚI CHO 3 TRƯỜNG HỢP THÔNG BÁO =====

    @Autowired
    private UserSubRepo userSubRepo;

    /**
     * 1. Gửi thông báo khi bài đăng được duyệt (APPROVED)
     */
    public void notifyListingApproved(User user, Listing listing) {
        String message = String.format(
            "🎉 Bài đăng '%s' của bạn đã được duyệt và đang hiển thị công khai!",
            listing.getTitle()
        );
        createNotificationForUser(user, message);
    }

    /**
     * 1.1. Gửi thông báo khi bài đăng bị từ chối (REJECTED)
     */
    public void notifyListingRejected(User user, Listing listing) {
        String message = String.format(
            "❌ Bài đăng '%s' của bạn đã bị từ chối. Vui lòng kiểm tra lại nội dung và gửi lại.",
            listing.getTitle()
        );
        createNotificationForUser(user, message);
    }

    /**
     * 2. Gửi thông báo khi có người review/comment
     */
    public void notifyNewReview(User reviewedUser, User reviewer, int rating, String comment) {
        String stars = "⭐".repeat(rating);
        String message = String.format(
            "💬 %s đã đánh giá bạn %s (%d/5 sao)%s",
            reviewer.getUserName(),
            stars,
            rating,
            comment != null && !comment.isEmpty() ? ": \"" + comment + "\"" : ""
        );
        createNotificationForUser(reviewedUser, message);
    }

    /**
     * 3. Gửi thông báo khi gói subscription sắp hết hạn (trước 2 ngày)
     * Chạy tự động mỗi ngày lúc 9:00 sáng
     */
    @Scheduled(cron = "0 0 9 * * *") // Chạy lúc 9:00 AM mỗi ngày
    public void checkExpiringSubscriptions() {
        // Tính ngày sau 2 ngày
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        Date twoDaysLater = calendar.getTime();

        // Tính cuối ngày sau 2 ngày
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.HOUR_OF_DAY, 23);
        calendar2.set(Calendar.MINUTE, 59);
        calendar2.set(Calendar.SECOND, 59);
        calendar2.add(Calendar.DAY_OF_MONTH, 2);
        Date endOfTwoDaysLater = calendar2.getTime();

        // Tìm các subscription sắp hết hạn trong 2 ngày tới
        List<User_Subscription> expiringSubs = userSubRepo.findByEndDateBetween(twoDaysLater, endOfTwoDaysLater);

        for (User_Subscription userSub : expiringSubs) {
            String message = String.format(
                "⚠️ Gói '%s' của bạn sẽ hết hạn vào %s. Gia hạn ngay để tiếp tục sử dụng!",
                userSub.getSubscriptionId().getSubName(),
                formatDate(userSub.getEndDate())
            );
            createNotificationForUser(userSub.getUser(), message);
        }

        System.out.println("✅ Checked " + expiringSubs.size() + " expiring subscriptions");
    }

    /**
     * Tạo notification cho user (internal method)
     */
    private void createNotificationForUser(User user, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setCreatedTime(new Date());
        notification.setStatus(NotificationStatus.ACTIVE);
        notificationRepo.save(notification);
        
        System.out.println("📩 Notification sent to " + user.getUserEmail() + ": " + message);
    }

    /**
     * Format date DD/MM/YYYY
     */
    private String formatDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return String.format("%02d/%02d/%d", 
            cal.get(Calendar.DAY_OF_MONTH),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.YEAR)
        );
    }

}
