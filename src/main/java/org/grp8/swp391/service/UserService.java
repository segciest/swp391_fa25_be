package org.grp8.swp391.service;

import org.grp8.swp391.dto.request.RegisterRequest;
import org.grp8.swp391.dto.request.UpdateUserRequest;
import org.grp8.swp391.entity.Role;
import org.grp8.swp391.entity.Subscription;
import org.grp8.swp391.entity.User;
import org.grp8.swp391.entity.UserStatus;
import org.grp8.swp391.repository.RoleRepo;
import org.grp8.swp391.repository.SubRepo;
import org.grp8.swp391.repository.UserRepo;
import org.grp8.swp391.repository.UserSubRepo;
import org.grp8.swp391.entity.User_Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SubRepo subRepo;

    @Autowired
    private UserSubRepo userSubRepo;


    @Autowired
    private RoleRepo roleRepo;

    public User updateUserRole(String id, Long roleId){
        User u = userRepo.findByUserID(id);
        if(u == null){
            throw new RuntimeException("User not found with id: " + id);
        }
        Role role = roleRepo.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));
        u.setRole(role);
        return userRepo.save(u);


    }


    public User updateUSerStatus(String id, UserStatus userStatus){
        User u = userRepo.findByUserID(id);
        if(u == null){
            throw new RuntimeException("User not found with id: " + id);
        }
        u.setUserStatus(userStatus);
        return userRepo.save(u);
    }


    public User login(String email, String password) {
        User user = userRepo.findByUserEmail(email);
        if (user == null) {
            throw new RuntimeException("Invalid email or password");
        }

        if (!passwordEncoder.matches(password, user.getUserPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        if(!user.getUserEmail().equalsIgnoreCase(email)){
            throw new RuntimeException("Invalid email or password");
        }

        if (user.getUserStatus() != UserStatus.ACTIVE) {
            throw new RuntimeException("Your account is being Banned or Pending. Please contact admin for more information.");
        }
        return user;
    }

    public User findByUserEmail(String email){


        return userRepo.findByUserEmail(email);
    }

    public void deleteById(String id){
        User check = userRepo.findByUserID(id);
        if (check == null) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepo.delete(check);
    }

    public User save(User user){
        return userRepo.save(user);
    }

    public User updateUser(UpdateUserRequest up, String id){
        User check = userRepo.findByUserID(id);
        if (check == null) {
            throw new RuntimeException("User not found with id: " + id);
        }

        if (up.getUserName() != null) {
            check.setUserName(up.getUserName());
        }
        if (up.getUserEmail() != null && !up.getUserEmail().equalsIgnoreCase(check.getUserEmail())) {
            if (userRepo.findByUserEmail(up.getUserEmail()) != null) {
                throw new RuntimeException("Email already in use");
            }
            check.setUserEmail(up.getUserEmail());
        }
        if (up.getPassword() != null && !up.getPassword().isBlank()) {
            check.setUserPassword(passwordEncoder.encode(up.getPassword()));
        }
        if (up.getDob() != null) {
            check.setDob(up.getDob());
        }

        if (up.getPhone() != null) {
            if (userRepo.findByPhone(up.getPhone()) != null) {
                throw new RuntimeException("Phone already in use");
            }
            check.setPhone(up.getPhone());
        }

        return userRepo.save(check);
    }

    public User registerUser(RegisterRequest req){
        if (userRepo.findByUserEmail(req.getUserEmail()) != null) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setUserName(req.getUserName());
        user.setUserEmail(req.getUserEmail());
        if (userRepo.findByPhone(req.getPhone()) != null) {
            throw new RuntimeException("Phone already in use");
        }
        user.setPhone(req.getPhone());
        user.setDob(req.getDob());
        user.setUserStatus(UserStatus.PENDING);

        Role defaultRole = roleRepo.findByRoleName("USER");
        if (defaultRole == null) {
            throw new RuntimeException("Default role USER not found");
        }
        user.setRole(defaultRole);


        Subscription freeSub = subRepo.findById(1L)
                .orElseThrow(() -> new RuntimeException("Default FREE subscription (ID=1) not found"));
        user.setSubid(freeSub);

        user.setUserPassword(passwordEncoder.encode(req.getUserPassword()));

        // First save the user so it has an ID for the User_Subscription foreign key
        User saved = userRepo.save(user);

        // Create and persist a User_Subscription record referencing the saved user
        try {
            User_Subscription us = new User_Subscription();
            us.setUser(saved);
            us.setSubscriptionId(freeSub);
            us.setStartDate(new java.util.Date());
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(us.getStartDate());
            cal.add(java.util.Calendar.DAY_OF_MONTH, freeSub.getDuration());
            us.setEndDate(cal.getTime());
            us.setDuration(freeSub.getDuration());
            us.setStatus("ACTIVE");

            userSubRepo.save(us);

            // update user's subid to reference the saved subscription
            saved.setSubid(freeSub);
            saved = userRepo.save(saved);
        } catch (Exception ex) {
            // Log a warning; don't block registration
            System.err.println("Warning: failed to create user subscription: " + ex.getMessage());
        }

        return saved;
    }

    public List<User> getAllUsers(){
        return userRepo.findAll();
    }

    public User findUserById(String id){
        return userRepo.findByUserID(id);
    }
}

