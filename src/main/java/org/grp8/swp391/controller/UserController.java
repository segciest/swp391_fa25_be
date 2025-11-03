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

    @PutMapping("/user-avatar")
    public ResponseEntity<?> changeUserAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request){
        try{
            String token = jwtUtils.extractToken(request);
            if (token == null || !jwtUtils.checkValidToken(token)) {
                return ResponseEntity.status(401).body("Invalid or missing token");
            }
            String email = jwtUtils.getUsernameFromToken(token);
            User u = userService.findByUserEmail(email);
            if (u == null) {
                return ResponseEntity.status(401).body("Invalid or missing token");
            }

            User changeAvatar = userService.changeUserAvatar(u.getUserID(), file);
            return ResponseEntity.ok(Map.of(
                    "message", "Avatar updated successfully!",
                    "avatarUrl", changeAvatar.getAvatarUrl()
            ));

        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
            res.setSubName(user.getSubid().getSubName());
            res.setDob(user.getDob());
            res.setRole(user.getRole().getRoleName());
            res.setToken(token);
            return ResponseEntity.ok(res);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/city")
    public ResponseEntity<?> findByUserLocation(@RequestParam String city){
        try{
            List<User> u = userService.findByUserCity(city);
            return ResponseEntity.ok(u);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MODERATOR')")

    @PutMapping("/role/{id}")
    public ResponseEntity<?> updateRole(@PathVariable String id,@RequestParam Long roleId){
        try{
            User u = userService.updateUserRole(id,roleId);
            return ResponseEntity.ok(u);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String otp, HttpServletRequest request) {
        try {
            String token = jwtUtils.extractToken(request);
            if (token == null || !jwtUtils.checkValidToken(token)) {
                return ResponseEntity.status(401).body("Invalid or missing token");
            }

            String email = jwtUtils.getUsernameFromToken(token);
            boolean verified = userService.verifyOtpCode(email, otp);

            if (!verified) {
                return ResponseEntity.badRequest().body("Mã OTP không hợp lệ hoặc đã hết hạn.");
            }

            return ResponseEntity.ok(Map.of("message", "Xác minh email thành công! Tài khoản đã được kích hoạt."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MODERATOR')")

    @PutMapping("/active/{id}")

    public ResponseEntity<?> updateUserStatusActive(@PathVariable String id){
        try{
            User u = userService.updateUSerStatus(id,UserStatus.ACTIVE);
            return ResponseEntity.ok(u);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MODERATOR')")

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

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            userService.sendResetPasswordOtp(email);
            String resetToken = jwtUtils.generateResetToken(email, 10); // 10 phút
            return ResponseEntity.ok(Map.of(
                    "message", "OTP đã được gửi đến email của bạn.",
                    "resetToken", resetToken
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/verify-reset-otp")
    public ResponseEntity<?> verifyResetOtp(HttpServletRequest request, @RequestParam String otp) {
        String token = jwtUtils.extractToken(request);
        if (token == null || !jwtUtils.checkValidToken(token)) {
            return ResponseEntity.status(401).body("Invalid or missing token");
        }

        String email = jwtUtils.getEmailFromResetToken(token);
        boolean verified = userService.verifyResetOtp(email, otp);

        if (!verified) {
            return ResponseEntity.badRequest().body("OTP không hợp lệ hoặc đã hết hạn.");
        }

        return ResponseEntity.ok(Map.of("message", "OTP hợp lệ, bạn có thể đặt lại mật khẩu."));
    }


    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(HttpServletRequest request, @RequestParam String newPass) {
        String token = jwtUtils.extractToken(request);
        if (token == null || !jwtUtils.checkValidToken(token)) {
            return ResponseEntity.status(401).body("Invalid or missing token");
        }

        String email = jwtUtils.getEmailFromResetToken(token);
        userService.resetUserPassword(email, newPass);

        return ResponseEntity.ok(Map.of("message", "Mật khẩu đã được đặt lại thành công!"));
    }


    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(HttpServletRequest request) {
        String token = jwtUtils.extractToken(request);
        if (token == null || !jwtUtils.checkValidToken(token)) {
            return ResponseEntity.status(401).body("Invalid or missing token");
        }

        String email = jwtUtils.getUsernameFromToken(token);
        userService.sendEmailVerification(email);
        return ResponseEntity.ok(Map.of("message", "Mã xác thực mới đã được gửi đến email của bạn."));
    }






}


