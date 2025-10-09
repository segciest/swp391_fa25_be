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
    public ResponseEntity<?> createUserSub(@RequestParam String userId, @RequestParam Long subId) {
        try {
            User_Subscription created = userSubService.createUserSub(userId, subId);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
    public ResponseEntity<?> updateUserSub(@PathVariable Long id,@RequestBody User_Subscription userSub) {
        try {
            userSubService.updateUserSub(id, userSub);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/remainday/{userId}")
    public ResponseEntity<?> getRemainingDays(@PathVariable String userId){
        try{
            User u = new User();
            u.setUserID(userId);

            int day = userSubService.getRemainingDate(u);
            return ResponseEntity.ok().body(day);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().build();
        }

    }


}
