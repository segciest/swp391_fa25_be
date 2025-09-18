package org.grp8.swp391.service;


import org.grp8.swp391.entity.Subscription;
import org.grp8.swp391.entity.User;
import org.grp8.swp391.repository.SubRepo;
import org.grp8.swp391.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SubService {
    @Autowired
    private SubRepo subRepo;


    @Autowired
    private UserRepo userRepo;


    public Subscription findById(Long id){
        return subRepo.findBySubId(id);
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

    public Subscription update(Subscription subscription){
        return subRepo.save(subscription);
    }

    public void deleteById(Long id){
        subRepo.deleteById(id);
    }

    public Subscription findByStatus(String status){
        return subRepo.findByStatus(status);
    }

    public User subPackage(String userId,Long subId){
        User user = userRepo.findByUserID(userId);
        if(user==null){
            throw new RuntimeException("User not found");
        }

        Subscription sub = subRepo.findBySubId(subId);
        if(sub==null){
            throw new RuntimeException("Sub not found");
        }

        user.setSubid(sub);
        return userRepo.save(user);
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

        user.setSubid(null);
        return userRepo.save(user);
    }


    public List<Subscription> deleteAll(){
        return subRepo.findAll();
    }


}
