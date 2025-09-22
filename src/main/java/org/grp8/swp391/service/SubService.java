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

    public Subscription findById(Long id) {
        return subRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found with id: " + id));
    }

    public Subscription findByName(String name) {
        return subRepo.findBySubName(name);
    }

    public List<Subscription> findAll() {
        return subRepo.findAll();
    }

    public Subscription create(Subscription subscription) {
        return subRepo.save(subscription);
    }

    public Subscription update(Subscription subscription) {
        return subRepo.save(subscription);
    }

    public void deleteById(Long id) {
        subRepo.deleteById(id);
    }

    public Subscription findByStatus(String status){
        return subRepo.findByStatus(status);
    }

    public User subPackage(String userId, Long subId) {
        User user = userRepo.findByUserID(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Subscription sub = findById(subId);
        user.setSubid(sub);
        return userRepo.save(user);
    }

    public User cancelPackage(String userId) {
        User user = userRepo.findByUserID(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        Subscription defaultSub = findById(1L);
        user.setSubid(defaultSub);
        return userRepo.save(user);
    }
}
