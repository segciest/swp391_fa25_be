
package org.grp8.swp391.service;

import com.cloudinary.Cloudinary;
import jakarta.transaction.Transactional;
import org.grp8.swp391.dto.request.RegisterRequest;
import org.grp8.swp391.dto.request.UpdateUserRequest;
import org.grp8.swp391.entity.*;
import org.grp8.swp391.repository.RoleRepo;
import org.grp8.swp391.repository.SubRepo;
import org.grp8.swp391.repository.UserRepo;
import org.grp8.swp391.repository.UserSubRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private EmailVerifyService emailVerifyService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserSubRepo userSubRepo;

    @Autowired
    private SubRepo subRepo;




    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private CloudinaryService cloudinaryService;

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
    @Transactional
    public void deleteById(String id){
        User check = userRepo.findByUserID(id);
        if (check == null) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userSubRepo.deleteByUser_UserID(id);
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

        if (userRepo.findByPhone(req.getPhone()) != null) {
            throw new RuntimeException("Phone already in use");
        }


        User user = new User();
        user.setUserName(req.getUserName());
        user.setUserEmail(req.getUserEmail());
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
        User savedUser = userRepo.save(user);

        User_Subscription userSub = new User_Subscription();
        userSub.setUser(savedUser);
        userSub.setSubscriptionId(freeSub);

        Date startDate = new Date();
        userSub.setStartDate(startDate);

        if (freeSub.getDuration() > 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);
            cal.add(Calendar.DAY_OF_MONTH, freeSub.getDuration());
            userSub.setEndDate(cal.getTime());
        } else {

            userSub.setEndDate(null);
        }

        userSub.setStatus("ACTIVE");





        userSubRepo.save(userSub);

        return savedUser;
    }

    public List<User> getAllUsers(){
        return userRepo.findAll();
    }

    public User findUserById(String id){
        return userRepo.findByUserID(id);
    }


    public User updateUserAvatar(String userId, MultipartFile file){
        User u = userRepo.findByUserID(userId);
        if (u == null) {
            throw new RuntimeException("User not found with id: " + userId);
        }

        String url = cloudinaryService.uploadFile(file);
        u.setAvatarUrl(url);
        return userRepo.save(u);
    }


    public Boolean verifyOtpCode(String email, String otp){
        User u = userRepo.findByUserEmail(email);
        if (u == null) {
            throw new RuntimeException("User not found with email: " + email);
        }

        if(u.getVerifiedCode() == null){
            throw new RuntimeException("Verification code not found");
        }

        if(!u.getVerifiedCode().equals(otp)){
            throw new RuntimeException("Verification code does not match");
        }
        u.setVerifiedCode(null);
        userRepo.save(u);
        return true;

    }
}
