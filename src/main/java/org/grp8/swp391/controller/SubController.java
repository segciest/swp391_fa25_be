package org.grp8.swp391.controller;

import org.grp8.swp391.entity.Subscription;
import org.grp8.swp391.entity.User;
import org.grp8.swp391.service.SubService;
import org.grp8.swp391.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscription")
public class SubController {
    @Autowired
    private SubService subService;
    @Autowired
    private UserService userService;


    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            Subscription sub = subService.findById(id);
            return ResponseEntity.ok().body(sub);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @GetMapping
    public ResponseEntity<?> findAll() {
        try {
            List<Subscription> subs = subService.findAll();
            return ResponseEntity.ok().body(subs);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/name")
    public ResponseEntity<?> findByName(@RequestParam String name) {
        try {
            Subscription sub = subService.findByName(name);
            return ResponseEntity.ok().body(sub);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/updateSub/{id}")
    public ResponseEntity<?> updateSubscription(@PathVariable Long id ,@RequestBody Subscription subscription) {
        try {
            Subscription sub = subService.findById(id);
            return ResponseEntity.ok(subService.update(subscription));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Subscription subscription) {
        try {
            Subscription sub = subService.create(subscription);
            return ResponseEntity.ok().body(sub);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        try {
            Subscription sub = subService.findById(id);
            subService.deleteById(id);
            return ResponseEntity.ok().body(sub);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    @PostMapping("/Subscription")
    public ResponseEntity<?> SubPackage(@RequestParam String userId,@RequestParam Long subId) {
        try{
            User u = userService.findUserById(userId);
            if(u==null){
                return ResponseEntity.badRequest().body("User not found");
            }
            Subscription sub = subService.findById(subId);
            if(sub==null){
                return ResponseEntity.badRequest().body("Subscription not found");
            }
            u.setSubid(sub);
            User updateUser = userService.save(u);
            return ResponseEntity.ok().body(updateUser);

        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/cacleSub")
    public ResponseEntity<?> cancleSubscription(String userId,Long subId) {
        try {
             User u = userService.findUserById(userId);
            if(u==null){
                return ResponseEntity.badRequest().body("User not found");
            }
            Subscription sub = subService.findById(subId);
            if(sub==null){
                return ResponseEntity.badRequest().body("Subscription not found");
            }
            u.setSubid(null);
            User updateUser = userService.save(u);
            return ResponseEntity.ok().body(updateUser);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
