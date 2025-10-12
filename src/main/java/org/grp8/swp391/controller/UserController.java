package org.grp8.swp391.controller;


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

import java.util.List;

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
            res.setRoleName(newUser.getRole());
            res.setStatus(newUser.getUserStatus());
            return ResponseEntity.ok(res);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PreAuthorize("hasAuthority('ADMIN')")

    @GetMapping("/list")
    public List<User> getAllUser() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id){
        User user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }
    @PreAuthorize("hasAuthority('ADMIN')")
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

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UpdateUserRequest us, @PathVariable String id){
        try{
            User u = userService.updateUser(us,id);
            RegisterResponse res = new RegisterResponse();
            res.setUserId(u.getUserID());
            res.setUserName(u.getUserName());
            res.setPhone(u.getPhone());
            res.setUserEmail(u.getUserEmail());
            res.setRoleName(u.getRole());
            res.setStatus(u.getUserStatus());


            return ResponseEntity.ok(res);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PreAuthorize("hasAuthority('ADMIN')")
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

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/role/{id}")
    public ResponseEntity<?> updateRole(@PathVariable String id,@RequestParam Long roleId){
        try{
            User u = userService.updateUserRole(id,roleId);
            return ResponseEntity.ok(u);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/active/{id}")

    public ResponseEntity<?> updateUserStatusActive(@PathVariable String id){
        try{
            User u = userService.updateUSerStatus(id,UserStatus.ACTIVE);
            return ResponseEntity.ok(u);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/ban/{id}")
    public ResponseEntity<?> updateUserStatusBanned(@PathVariable String id){
        try{
            User u = userService.updateUSerStatus(id,UserStatus.BANNED);
            return ResponseEntity.ok(u);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }

}


