package org.grp8.swp391.controller;

import org.grp8.swp391.service.AdminDashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminDashController {

    @Autowired
    private AdminDashService adminDashService;

    // ------------------- USERS -------------------
    @GetMapping("/users")
    public ResponseEntity<?> getUserStats() {
        return ResponseEntity.ok(Map.of(
                "totalUsers", adminDashService.getTotalUsers(),
                "activeUsers", adminDashService.getActiveUsers(),
                "bannedUsers", adminDashService.getBannedUsers(),
                "pendingUsers", adminDashService.getPendingUsers()
        ));
    }



    // ------------------- SUBSCRIPTIONS -------------------
    @GetMapping("/subscriptions")
    public ResponseEntity<?> getSubscriptionStats() {
        return ResponseEntity.ok(Map.of(
                "freeUsers", adminDashService.getFreeUsers(),
                "basicUsers", adminDashService.getBasicUsers(),
                "standardUsers", adminDashService.getStandardUsers(),
                "premiumUsers", adminDashService.getPremiumUsers(),
                "vipUsers", adminDashService.getVIPUsers()
        ));
    }

    @GetMapping("/subscriptions-growth")
    public ResponseEntity<?> getSubscriptionGrowth() {
        return ResponseEntity.ok(Map.of(
                "subscriptions", Map.of(
                        "weekly", adminDashService.getSubscriptionWeeklyGrowth(),
                        "monthly", adminDashService.getSubscriptionMonthlyGrowth(),
                        "quarterly", adminDashService.getSubscriptionQuarterlyGrowth(),
                        "yearly", adminDashService.getSubscriptionYearlyGrowth()
                )
        ));
    }

    @GetMapping("/listings")
    public ResponseEntity<?> getListingStats() {
        return ResponseEntity.ok(Map.of(
                "activeListings", adminDashService.getActiveListing(),
                "pendingListings", adminDashService.getPendingListing(),
                "bannedListings", adminDashService.getBannedListing()
        ));
    }

    @GetMapping("/listings-growth")
    public ResponseEntity<?> getListingGrowth() {
        return ResponseEntity.ok(Map.of(
                "listings", Map.of(
                        "weekly", adminDashService.getListingWeeklyGrowth(),
                        "monthly", adminDashService.getListingMonthlyGrowth(),
                        "quarterly", adminDashService.getListingQuarterlyGrowth(),
                        "yearly", adminDashService.getListingYearlyGrowth()
                )
        ));
    }

    @GetMapping("/reports")
    public ResponseEntity<?> getReportStats() {
        return ResponseEntity.ok(Map.of(
                "pendingReports", adminDashService.getPendingReports(),
                "resolvedReports", adminDashService.getResolvedReports(),
                "rejectedReports", adminDashService.getRejectedReports()
        ));
    }

    @GetMapping("/reports-growth")
    public ResponseEntity<?> getReportGrowth() {
        return ResponseEntity.ok(Map.of(
                "reports", Map.of(
                        "weekly", adminDashService.getReportWeeklyGrowth(),
                        "monthly", adminDashService.getReportMonthlyGrowth(),
                        "quarterly", adminDashService.getReportQuarterlyGrowth(),
                        "yearly", adminDashService.getReportYearlyGrowth()
                )
        ));
    }

    // ------------------- REVENUE -------------------
    @GetMapping("/revenue")
    public ResponseEntity<?> getRevenueStats() {
        return ResponseEntity.ok(Map.of(
                "totalRevenue", adminDashService.getTotalRevenue(),
                                "monthlyRevenue", adminDashService.getMonthlyRevenue(),
                                "quarterlyRevenue", adminDashService.getRevenueQuarterlyGrowth()
        ));
    }

    @GetMapping("/revenue-growth")
    public ResponseEntity<?> getRevenueGrowth() {
        return ResponseEntity.ok(Map.of(
                "revenue", Map.of(
                        "weekly", adminDashService.getRevenueWeeklyGrowth(),
                        "monthly", adminDashService.getRevenueMonthlyGrowth(),
                        "quarterly", adminDashService.getRevenueQuarterlyGrowth(),
                        "yearly", adminDashService.getRevenueYearlyGrowth()
                )
        ));
    }

    // ------------------- USER REGISTRATION -------------------
    @GetMapping("/registrations")
    public ResponseEntity<?> getUserRegistrations() {
        return ResponseEntity.ok(adminDashService.getMonthlyUserRegistrations());
    }
}
