package org.grp8.swp391.controller;


import org.grp8.swp391.config.JwtUtils;
import org.grp8.swp391.dto.request.LoginRequest;
import org.grp8.swp391.dto.response.LoginResponse;
import org.grp8.swp391.dto.response.RegisterResponse;
import org.grp8.swp391.entity.User;
import org.grp8.swp391.entity.UserStatus;
import org.grp8.swp391.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> register(@RequestBody User user) {
        try{
            User newUser = userService.registerUser(user);
            return ResponseEntity.ok(newUser);
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

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@RequestBody User us, @PathVariable String id){
        try{
            User u = userService.updateUser(us,id);
            RegisterResponse res = new RegisterResponse();
            res.setUserId(u.getUserID());
            res.setUserName(u.getUserName());
            res.setUserEmail(u.getUserEmail());
            res.setPhone(u.getPhone());
            res.setUserEmail(u.getUserEmail());
            res.setRoleName(u.getRole());
            res.setStatus(u.getUserStatus());


            return ResponseEntity.ok(res);
        }catch(RuntimeException e){
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
            User user = userService.login(request.getUserEmail(), request.getUserPassword());
            String token = jwtUtils.generateToken(user.getUserEmail(),user.getRole().getRoleName());


            LoginResponse res = new LoginResponse();
            res.setUserName(user.getUserName());
            res.setUserEmail(user.getUserEmail());
            res.setUserStatus(user.getUserStatus().name());

            res.setDob(user.getDob());
            res.setRole(user.getRole());
            res.setToken(token);
            return ResponseEntity.ok(res);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
