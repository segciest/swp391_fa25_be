package org.grp8.swp391.service;


import org.grp8.swp391.entity.Subscription;
import org.grp8.swp391.entity.User;
import org.grp8.swp391.entity.UserStatus;
import org.grp8.swp391.entity.User_Subscription;
import org.grp8.swp391.repository.SubRepo;
import org.grp8.swp391.repository.UserRepo;
import org.grp8.swp391.repository.UserSubRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Service
public class SubService {
    @Autowired
    private SubRepo subRepo;

    @Autowired
    private UserSubRepo userSubRepo;

    @Autowired
    private UserRepo userRepo;


    public Subscription findById(Long id){
        return subRepo.findById(id).orElseThrow(() -> new RuntimeException("Subscription not found with id: " + id));
    }


    public Subscription findByName(String name){
        return  subRepo.findBySubName(name);
    }

    public List<Subscription> findAll(){
        return subRepo.findAll();
    }

    public Subscription create(Subscription subscription){

        return subRepo.save(subscription);
    }

    public Subscription update(Long subId,Subscription subscription){
        Subscription sub = subRepo.findBySubId(subId);
        if(sub==null){
            throw new RuntimeException("Subscription not found with id: " + subId);
        }
        if(subscription.getSubName()!=null){
            sub.setSubName(subscription.getSubName());
        }
        if(subscription.getSubDetails()!=null){
            sub.setSubDetails(subscription.getSubDetails());
            //check user detail 
        }
        if(subscription.getSubPrice()!=null) {
            sub.setSubPrice(subscription.getSubPrice());
        }
        if(subscription.getDuration() != 0 ){
            sub.setDuration(subscription.getDuration());

        }

        if(subscription.getPriorityLevel() != 0){
            sub.setPriorityLevel(subscription.getPriorityLevel());
        }

        if(subscription.getStatus()!=null){
            sub.setStatus(subscription.getStatus());
        }

        return subRepo.save(sub);

    }

    public void deleteById(Long id){
        subRepo.deleteById(id);
    }

    public Subscription findByStatus(String status){
        return subRepo.findByStatus(status);
    }

    public User subPackage(String userId,Long subId){
        User user = userRepo.findByUserID(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (user.getUserStatus() != UserStatus.ACTIVE) {
            throw new RuntimeException("Please verify your email before purchasing a package.");
        }

        Subscription sub = subRepo.findBySubId(subId);
        if (sub == null) {
            throw new RuntimeException("Subscription not found");
        }

        // ✅ CHECK: CHỈ CHO PHÉP đăng ký FREE qua endpoint này
        if (!sub.getSubName().equalsIgnoreCase("Free")) {
            throw new RuntimeException("This endpoint is only for FREE subscriptions. Please use VNPay payment for paid subscriptions.");
        }

        // ✅ CHECK: User đã có Free ACTIVE chưa (ngăn tạo Free thứ 2)
        List<User_Subscription> userSubs = userSubRepo.findByUser(user);
        boolean hasActiveFree = userSubs.stream()
            .anyMatch(s -> "Free".equalsIgnoreCase(s.getSubscriptionId().getSubName()) && 
                          "ACTIVE".equals(s.getStatus()));
        
        if (hasActiveFree) {
            throw new RuntimeException("You already have an active Free subscription. Each user can only have one Free subscription.");
        }

        // ✅ HỦY tất cả subscription ACTIVE cũ (nếu có paid đang active)
        for (User_Subscription activeSub : userSubs) {
            if ("ACTIVE".equals(activeSub.getStatus())) {
                activeSub.setStatus("CANCELLED");
                userSubRepo.save(activeSub);
                System.out.println("🔄 Cancelled old subscription: " + activeSub.getSubscriptionId().getSubName());
            }
        }

        User_Subscription userSub = new User_Subscription();

        userSub.setUser(user);
        userSub.setSubscriptionId(sub);
        userSub.setStatus("ACTIVE");

        Date startDate = new Date();
        userSub.setStartDate(startDate);

        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.DAY_OF_MONTH, sub.getDuration());
        userSub.setEndDate(cal.getTime());

        userSubRepo.save(userSub);

        user.setSubid(sub);
        userRepo.save(user);

        return user;
    }

    public User canclePackage(String userId,Long subId){
        User user = userRepo.findByUserID(userId);
        if(user==null){
            throw new RuntimeException("User not found");
        }

        Subscription sub = subRepo.findBySubId(subId);
        if(sub==null){
            throw new RuntimeException("Sub not found");
        }
        User_Subscription current = userSubRepo.findFirstByUserOrderByEndDateDesc(user);
        if (current == null || !"ACTIVE".equalsIgnoreCase(current.getStatus())) {
            throw new RuntimeException("No active subscription found to cancel");
        }

        current.setStatus("CANCELLED");
        userSubRepo.save(current);
        user.setSubid(null);
        return userRepo.save(user);
    }

    public void checkExpiredSubscription(){
        Date now = new Date();
        List<User_Subscription> activeSub = userSubRepo.findByStatus("ACTIVE");
        for (User_Subscription us : activeSub) {
            if(us.getEndDate() != null && us.getEndDate().before(now)){
                us.setStatus("EXPIRED");
                userSubRepo.save(us);
                System.out.println("📅 Expired: " + us.getSubscriptionId().getSubName() 
                    + " for user " + us.getUser().getUserID());
            }
        }
    }




}
