package org.grp8.swp391.service;

import org.grp8.swp391.entity.ListingStatus;
import org.grp8.swp391.entity.PaymentStatus;
import org.grp8.swp391.repository.ListingRepo;
import org.grp8.swp391.repository.PaymentRepo;
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


    public Long getTotalUsers() {
        return userRepo.count();
    }

    public Long getActiveListing(){
        return listingRepo.countByStatus(ListingStatus.ACTIVE);
    }

    public Double getTotalRevenue(){
        return paymentRepo.sumAmountByStatus(PaymentStatus.COMPLETED);
    }



}
