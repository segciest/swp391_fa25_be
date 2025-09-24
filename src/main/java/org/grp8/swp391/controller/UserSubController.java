package org.grp8.swp391.controller;

import org.grp8.swp391.entity.Subscription;
import org.grp8.swp391.entity.User;
import org.grp8.swp391.entity.User_Subscription;
import org.grp8.swp391.service.UserSubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/UserSub")
public class UserSubController {

    @Autowired
    private UserSubService userSubService;
    @PostMapping("/create")
    public ResponseEntity<?> createUserSub(User_Subscription userSub) {
        return ResponseEntity.ok(userSubService.createUserSub(userSub));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserSub(@PathVariable Long id) {
        userSubService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/userlist")
    public ResponseEntity<?> findUserBySub(Subscription sub){
        List<User_Subscription> check = userSubService.findUserBySubscription(sub);
        return ResponseEntity.ok(check);
    }
    @GetMapping("/sublist")
    public ResponseEntity<?> findSubByUser(User user){
        List<User_Subscription> check = userSubService.findSubByUser(user);
        return ResponseEntity.ok(check);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserSub(@PathVariable Long id,User_Subscription userSub) {
        try {
            userSubService.updateUserSub(id, userSub);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }


}
