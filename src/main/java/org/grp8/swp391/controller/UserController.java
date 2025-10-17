package org.grp8.swp391.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.grp8.swp391.config.JwtUtils;
import org.grp8.swp391.dto.request.LoginRequest;
import org.grp8.swp391.dto.request.RegisterRequest;
import org.grp8.swp391.dto.request.UpdateUserRequest;
import org.grp8.swp391.dto.response.LoginResponse;
import org.grp8.swp391.dto.response.RegisterResponse;
import org.grp8.swp391.entity.User;
import org.grp8.swp391.entity.UserStatus;
import org.grp8.swp391.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;



    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        try{
            User newUser = userService.registerUser(req);
            RegisterResponse res = new RegisterResponse();
            res.setUserId(newUser.getUserID());
            res.setUserName(newUser.getUserName());
            res.setUserEmail(newUser.getUserEmail());
            res.setPhone(newUser.getPhone());
            res.setRoleName(newUser.getRole().getRoleName());
            res.setStatus(newUser.getUserStatus());
            return ResponseEntity.ok(res);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/list")
    public List<User> getAllUser() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id){
        User user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<?> updateUserStatus(@PathVariable String id, @RequestParam String status){
        try {
            UserStatus newStatus = UserStatus.valueOf(status.toUpperCase());
            User user = userService.updateUSerStatus(id, newStatus);
            return ResponseEntity.ok(user);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserRequest req, HttpServletRequest request) {
        try {
            String token = jwtUtils.extractToken(request);
            if (token == null || !jwtUtils.checkValidToken(token)) {
                return ResponseEntity.status(401).body("Invalid or missing token");
            }

            String email = jwtUtils.getUsernameFromToken(token);
            User currentUser = userService.findByUserEmail(email);

            User updated = userService.updateUser(req, currentUser.getUserID());

            RegisterResponse res = new RegisterResponse();
            res.setUserId(updated.getUserID());
            res.setUserName(updated.getUserName());
            res.setPhone(updated.getPhone());
            res.setUserEmail(updated.getUserEmail());
            res.setRoleName(updated.getRole().getRoleName());
            res.setStatus(updated.getUserStatus());

            return ResponseEntity.ok(res);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id){
        User u = userService.findUserById(id);
        userService.deleteById(id);
        return  ResponseEntity.ok(u);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.login(request.getEmail(), request.getPassword());
            String token = jwtUtils.generateToken(user.getUserEmail(),user.getRole().getRoleName());


            LoginResponse res = new LoginResponse();
            res.setUserId(user.getUserID());
            res.setUserName(user.getUserName());
            res.setUserEmail(user.getUserEmail());
            res.setPhone(user.getPhone());
            res.setUserStatus(user.getUserStatus().name());

            res.setDob(user.getDob());
            res.setRole(user.getRole());
            res.setToken(token);
            return ResponseEntity.ok(res);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/role/{id}")
    public ResponseEntity<?> updateRole(@PathVariable String id,@RequestParam Long roleId){
        try{
            User u = userService.updateUserRole(id,roleId);
            return ResponseEntity.ok(u);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    //chua can dung den
    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam String email, @RequestParam String otp) {
        boolean success = userService.verifyOtpCode(email, otp);
        if (success) {
            return ResponseEntity.ok("Xác minh thành công! Bạn có thể đăng nhập ngay.");
        } else {
            return ResponseEntity.badRequest().body("Mã OTP không chính xác.");
        }
    }

    @PutMapping("/active/{id}")

    public ResponseEntity<?> updateUserStatusActive(@PathVariable String id){
        try{
            User u = userService.updateUSerStatus(id,UserStatus.ACTIVE);
            return ResponseEntity.ok(u);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }
    @PutMapping("/ban/{id}")
    public ResponseEntity<?> updateUserStatusBanned(@PathVariable String id){
        try{
            User u = userService.updateUSerStatus(id,UserStatus.BANNED);
            return ResponseEntity.ok(u);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }

    @PostMapping("/avatar")
    public ResponseEntity<?> updateAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request){
        String token = jwtUtils.extractToken(request);
        if(token == null || !jwtUtils.checkValidToken(token)){
            return ResponseEntity.badRequest().body("Token invalid");
        }

        String email = jwtUtils.getUsernameFromToken(token);
        User u = userService.findByUserEmail(email);
        if(u == null){
            return ResponseEntity.badRequest().body("User not found");
        }

        try{
            User up = userService.updateUserAvatar(u.getUserID(), file);
            return ResponseEntity.ok(Map.of( "message", "Avatar updated successfully","avatarUrl", up.getAvatarUrl()));
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

}


