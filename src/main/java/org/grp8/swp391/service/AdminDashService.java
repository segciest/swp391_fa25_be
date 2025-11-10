package org.grp8.swp391.service;

import org.grp8.swp391.entity.ListingStatus;
import org.grp8.swp391.entity.PaymentStatus;
import org.grp8.swp391.entity.ReportedStatus;
import org.grp8.swp391.entity.UserStatus;
import org.grp8.swp391.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminDashService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ListingRepo listingRepo;

    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private ReportRepo reportRepo;

    @Autowired
    private UserSubRepo userSubRepo;


    /** Tổng số người dùng */
    public Long getTotalUsers() {
        return userRepo.count();
    }

    /** Người dùng đang hoạt động */
    public Long getActiveUsers() {
        return userRepo.countByUserStatus(UserStatus.ACTIVE);
    }

    /** Người dùng bị khóa */
    public Long getBannedUsers() {
        return userRepo.countByUserStatus(UserStatus.BANNED);
    }

    /** Người dùng đang chờ xác thực */
    public Long getPendingUsers() {
        return userRepo.countByUserStatus(UserStatus.PENDING);
    }

    // ---------------------- SUBSCRIPTION ----------------------

    public Long getFreeUsers() { return userRepo.countUsersWithFreeSubscription(); }

    public Long getBasicUsers() { return userRepo.countUsersWithBasicSubscription(); }

    public Long getStandardUsers() { return userRepo.countUsersWithStandardSubscription(); }

    public Long getPremiumUsers() { return userRepo.countUsersWithPremiumSubscription(); }

    public Long getVIPUsers() { return userRepo.countUsersWithVIPSubscription(); }

    // ---------------------- LISTING ----------------------

    public Long getActiveListing() { return listingRepo.countByStatus(ListingStatus.ACTIVE); }

    public Long getPendingListing() { return listingRepo.countByStatus(ListingStatus.PENDING); }

    public Long getBannedListing() { return listingRepo.countByStatus(ListingStatus.BANNED); }

    // ---------------------- REPORT ----------------------

    public Long getPendingReports() { return reportRepo.countByStatus(ReportedStatus.PENDING); }

    public Long getResolvedReports() { return reportRepo.countByStatus(ReportedStatus.RESOLVED); }

    public Long getRejectedReports() { return reportRepo.countByStatus(ReportedStatus.REJECTED); }

    // ---------------------- REVENUE ----------------------

    /** Tổng doanh thu */
    public Double getTotalRevenue() {
        Double revenue = paymentRepo.sumAmountByStatus(PaymentStatus.COMPLETED);
        return revenue != null ? revenue : 0.0;
    }

    /** Doanh thu theo tháng trong năm hiện tại */
    public List<Map<String, Object>> getMonthlyRevenue() {
        List<Object[]> results = paymentRepo.getMonthlyRevenue();
        List<Map<String, Object>> response = new ArrayList<>();

        // Tạo đủ 12 tháng (1–12)
        for (int i = 1; i <= 12; i++) {
            Map<String, Object> month = new HashMap<>();
            month.put("month", i);
            month.put("revenue", 0.0);
            response.add(month);
        }

        // Ghi đè tháng có doanh thu thật
        for (Object[] row : results) {
            int month = ((Number) row[0]).intValue();
            double revenue = ((Number) row[1]).doubleValue();
            response.get(month - 1).put("revenue", revenue);
        }

        return response;
    }

    // ---------------------- USER REGISTRATION ----------------------

    public List<Map<String, Object>> getMonthlyUserRegistrations() {
        List<Object[]> results = userSubRepo.getMonthlyUserRegistrations();
        List<Map<String, Object>> response = new ArrayList<>();

        for (int i = 1; i <= 12; i++) {
            Map<String, Object> month = new HashMap<>();
            month.put("month", i);
            month.put("registrations", 0L);
            response.add(month);
        }

        // Cập nhật các tháng có dữ liệu thật
        for (Object[] row : results) {
            int month = ((Number) row[0]).intValue();
            long registrations = ((Number) row[1]).longValue();
            response.get(month - 1).put("registrations", registrations);
        }

        return response;
    }




    // LISTING
    public List<Map<String, Object>> getListingWeeklyGrowth() {
        return mapResults(listingRepo.getListingWeeklyGrowth(), "week", "count");
    }
    public List<Map<String, Object>> getListingMonthlyGrowth() {
        return mapResults(listingRepo.getListingMonthlyGrowth(), "month", "count");
    }
    public List<Map<String, Object>> getListingYearlyGrowth() {
        return mapResults(listingRepo.getListingYearlyGrowth(), "year", "count");
    }
    public List<Map<String, Object>> getListingQuarterlyGrowth() {
        return mapResults(listingRepo.getListingQuarterlyGrowth(), "quarter", "count");
    }

    // SUBSCRIPTION
    public List<Map<String, Object>> getSubscriptionWeeklyGrowth() {
        return mapResults(userSubRepo.getSubscriptionWeeklyGrowth(), "week", "activated");
    }
    public List<Map<String, Object>> getSubscriptionMonthlyGrowth() {
        return mapResults(userSubRepo.getSubscriptionMonthlyGrowth(), "month", "activated");
    }
    public List<Map<String, Object>> getSubscriptionYearlyGrowth() {
        return mapResults(userSubRepo.getSubscriptionYearlyGrowth(), "year", "activated");
    }
    public List<Map<String, Object>> getSubscriptionQuarterlyGrowth() {
        return mapResults(userSubRepo.getSubscriptionQuarterlyGrowth(), "quarter", "activated");
    }

    // REVENUE
    public List<Map<String, Object>> getRevenueWeeklyGrowth() {
        return mapResults(paymentRepo.getRevenueWeeklyGrowth(), "week", "amount");
    }
    public List<Map<String, Object>> getRevenueMonthlyGrowth() {
        return mapResults(paymentRepo.getRevenueMonthlyGrowth(), "month", "amount");
    }
    public List<Map<String, Object>> getRevenueYearlyGrowth() {
        return mapResults(paymentRepo.getRevenueYearlyGrowth(), "year", "amount");
    }
    public List<Map<String, Object>> getRevenueQuarterlyGrowth() {
        return mapResults(paymentRepo.getRevenueQuarterlyGrowth(), "quarter", "amount");
    }

    // REPORTS
    public List<Map<String, Object>> getReportWeeklyGrowth() {
        return mapResults(reportRepo.getReportWeeklyGrowth(), "week", "count");
    }
    public List<Map<String, Object>> getReportMonthlyGrowth() {
        return mapResults(reportRepo.getReportMonthlyGrowth(), "month", "count");
    }
    public List<Map<String, Object>> getReportQuarterlyGrowth() {
        return mapResults(reportRepo.getReportQuarterlyGrowth(), "quarter", "count");
    }
    public List<Map<String, Object>> getReportYearlyGrowth() {
        return mapResults(reportRepo.getReportYearlyGrowth(), "year", "count");
    }

    private List<Map<String, Object>> mapResults(List<Object[]> rows, String... keys) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object[] r : rows) {
            Map<String, Object> item = new HashMap<>();
            for (int i = 0; i < keys.length; i++) {
                item.put(keys[i], r[i]);
            }
            list.add(item);
        }
        return list;
    }



}
