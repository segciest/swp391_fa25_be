package org.grp8.swp391.service;

import jakarta.persistence.EntityNotFoundException;
import org.grp8.swp391.entity.Subscription;
import org.grp8.swp391.entity.User;
import org.grp8.swp391.entity.User_Subscription;
import org.grp8.swp391.repository.UserSubRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserSubService {
    @Autowired
    private UserSubRepo userSubRepo;

    public void deleteById(Long id) {
        if(!userSubRepo.existsById(id)){
            throw new EntityNotFoundException("UserSub with id " + id + " does not exist");
        }
        userSubRepo.deleteById(id);
    }

    public User_Subscription createUserSub(User_Subscription userSub) {
        userSub.setStartDate(new Date());
        return userSubRepo.save(userSub);
    }

    public User_Subscription updateUserSub(Long id,User_Subscription userSub) {
        User_Subscription check = userSubRepo.findByUserSubId(id);
        if(check == null){
            throw new RuntimeException("UserSub with id " + id + " does not exist");

        }

        if (userSub.getEndDate() != null) {
            check.setEndDate(userSub.getEndDate());
        }
        if (userSub.getStatus() != null) {
            check.setStatus(userSub.getStatus());
        }
        return  userSubRepo.save(check);

    }


    public List<User_Subscription> findUserBySubscription(Subscription sub) {
        return userSubRepo.findBySubscriptionId(sub);
    }

    public List<User_Subscription> findSubByUser(User user) {
        return userSubRepo.findByUser(user);
    }
}
