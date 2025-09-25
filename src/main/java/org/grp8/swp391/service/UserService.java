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

        if (req.getSubId() != null) {
            Subscription sub = subRepo.findById(req.getSubId())
                    .orElseThrow(() -> new RuntimeException("Subscription not found"));
            user.setSubid(sub);
        }

        // encode password
        user.setUserPassword(passwordEncoder.encode(req.getUserPassword()));

        return userRepo.save(user);
    }

    public List<User> getAllUsers(){
        return userRepo.findAll();
    }

    public User findUserById(String id){
        return userRepo.findByUserID(id);
    }
}

