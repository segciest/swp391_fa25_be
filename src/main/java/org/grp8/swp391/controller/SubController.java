package org.grp8.swp391.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.grp8.swp391.config.JwtUtils;
import org.grp8.swp391.entity.Subscription;
import org.grp8.swp391.entity.User;
import org.grp8.swp391.service.SubService;
import org.grp8.swp391.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subscription")
public class SubController {
    @Autowired
    private SubService subService;
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;


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
        try{
            Subscription sub = subService.update(id, subscription);
            return ResponseEntity.ok().body(sub);
        }catch (RuntimeException e){
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



    @PostMapping("/SubPackage")
    public ResponseEntity<?> SubPackage(@RequestParam Long subId, HttpServletRequest request) {
        try {

            String token = jwtUtils.extractToken(request);
            if (token == null || !jwtUtils.checkValidToken(token)) {
                return ResponseEntity.status(401).body("Missing or invalid token");
            }

            String email = jwtUtils.getUsernameFromToken(token);
            if (email == null) {
                return ResponseEntity.status(401).body("Invalid token payload");
            }

            User user = userService.findByUserEmail(email);
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found");
            }

            User updatedUser = subService.subPackage(user.getUserID(), subId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Đăng ký gói thành công!");
            response.put("subscription", updatedUser.getSubid());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    @PutMapping("/cancel")
    public ResponseEntity<?> cancleSubscription(@RequestParam Long subId, HttpServletRequest request) {
        try{
            String token = jwtUtils.extractToken(request);
            if (token == null || !jwtUtils.checkValidToken(token)) {
                return ResponseEntity.status(401).body("Invalid token payload");
            }
            String email = jwtUtils.getUsernameFromToken(token);
            if (email == null) {
                return ResponseEntity.status(401).body("Invalid token payload");
            }
            User user = userService.findByUserEmail(email);
            User updatedUser = subService.canclePackage(user.getUserID(), subId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Hủy gói thành công!");
            response.put("subscription", updatedUser.getSubid());
            return ResponseEntity.ok(response);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
