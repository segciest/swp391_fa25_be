package org.grp8.swp391.service;

import org.grp8.swp391.entity.ListingStatus;
import org.grp8.swp391.entity.PaymentStatus;
import org.grp8.swp391.entity.ReportedStatus;
import org.grp8.swp391.entity.UserStatus;
import org.grp8.swp391.repository.ListingRepo;
import org.grp8.swp391.repository.PaymentRepo;
import org.grp8.swp391.repository.ReportRepo;
import org.grp8.swp391.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


    public Long getTotalUsers() {
        return userRepo.count();
    }

    public Long getActiveUsers() {
        return userRepo.countByUserStatus(UserStatus.ACTIVE);
    }

    public Long getBannedUsers() {
        return userRepo.countByUserStatus(UserStatus.BANNED);
    }

    public Long getPendingUsers() {
        return userRepo.countByUserStatus(UserStatus.PENDING);
    }

    // ---------------------- SUBSCRIPTION ----------------------
    public Long getFreeUsers() {
        return userRepo.countUsersWithFreeSubscription();
    }

    public Long getBasicUsers() {
        return userRepo.countUsersWithBasicSubscription();
    }

    public Long getStandardUsers() {
        return userRepo.countUsersWithStandardSubscription();
    }

    public Long getPremiumUsers() {
        return userRepo.countUsersWithPremiumSubscription();
    }

    public Long getVIPUsers() {
        return userRepo.countUsersWithVIPSubscription();
    }

    // ---------------------- LISTING ----------------------
    public Long getActiveListing() {
        return listingRepo.countByStatus(ListingStatus.ACTIVE);
    }

    public Long getPendingListing() {
        return listingRepo.countByStatus(ListingStatus.PENDING);
    }

    public Long getBannedListing() {
        return listingRepo.countByStatus(ListingStatus.BANNED);
    }

    // ---------------------- REPORT ----------------------
    public Long getPendingReports() {
        return reportRepo.countByStatus(ReportedStatus.PENDING);
    }

    public Long getResolvedReports() {
        return reportRepo.countByStatus(ReportedStatus.RESOLVED);
    }

    public Long getRejectedReports() {
        return reportRepo.countByStatus(ReportedStatus.REJECTED);
    }

    // ---------------------- REVENUE ----------------------
    public Double getTotalRevenue() {
        Double revenue = paymentRepo.sumAmountByStatus(PaymentStatus.COMPLETED);
        return revenue != null ? revenue : 0.0;
    }



}
