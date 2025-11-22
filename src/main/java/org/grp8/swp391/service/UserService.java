package org.grp8.swp391.service;

import com.cloudinary.Cloudinary;
import jakarta.transaction.Transactional;
import org.grp8.swp391.config.JwtUtils;
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
import java.util.Random;

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
    @Autowired
    private JwtUtils jwtUtils;

    public User updateUserRole(String id, Long roleId){
        User u = userRepo.findByUserID(id);
        if(u == null){
            throw new RuntimeException("User not found with id: " + id);
        }
        Role role = roleRepo.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));
        u.setRole(role);
        return userRepo.save(u);


    }

    public User changeUserAvatar(String userId, MultipartFile file){
        User u = userRepo.findByUserID(userId);
        if(u == null){
            throw new RuntimeException("User not found with id: " + userId);

        }

        String url = cloudinaryService.uploadFile(file);
        u.setAvatarUrl(url);
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

    public List<User> findAllNormalUser(){
        List<User> u = userRepo.findAllByRole_RoleName("USER");
        if(u == null){
            throw new RuntimeException("User not found");
        }

        return u;

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


        if (user.getUserStatus() == UserStatus.BANNED ) {
            throw new RuntimeException("Your account is being Banned. Please contact admin for more information.");
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

    public User updateUser(UpdateUserRequest up, String id) {
        User check = userRepo.findByUserID(id);
        if (check == null) {
            throw new RuntimeException("User not found with id: " + id);
        }

        if (up.getUserName() != null && !up.getUserName().isBlank()) {
            check.setUserName(up.getUserName().trim());
        }

        if (up.getUserEmail() != null && !up.getUserEmail().equalsIgnoreCase(check.getUserEmail())) {
            User existing = userRepo.findByUserEmail(up.getUserEmail());
            if (existing != null && !existing.getUserID().equals(id)) {
                throw new RuntimeException("Email already in use by another account");
            }
            check.setUserEmail(up.getUserEmail().trim().toLowerCase());
        }

        if (up.getPassword() != null && !up.getPassword().isBlank()) {
            if (!passwordEncoder.matches(up.getPassword(), check.getUserPassword())) {
                check.setUserPassword(passwordEncoder.encode(up.getPassword()));
            }
        }

        if (up.getDob() != null) {
            check.setDob(up.getDob());
        }

        if (up.getPhone() != null && !up.getPhone().equals(check.getPhone())) {
            User existingPhone = userRepo.findByPhone(up.getPhone());
            if (existingPhone != null && !existingPhone.getUserID().equals(id)) {
                throw new RuntimeException("Phone already in use by another account");
            }
            check.setPhone(up.getPhone().trim());
        }

        if (up.getCity() != null) {
            check.setCity(up.getCity().trim());
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
        user.setCity(req.getCity());
        user.setUserStatus(UserStatus.PENDING);


        Role defaultRole = roleRepo.findByRoleName("USER");
        if (defaultRole == null) {
            throw new RuntimeException("Default role USER not found");
        }
        user.setRole(defaultRole);


        Subscription freeSub = subRepo.findById(1L)
                .orElseThrow(() -> new RuntimeException("Default FREE subscription (ID=1) not found"));
        user.setSubid(freeSub);

        //String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
        //user.setVerifiedCode(otp);


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
        
        // ✅ Set ACTIVE ngay khi đăng ký (user có thể dùng Free luôn)
        userSub.setStatus("ACTIVE");
/*
        String subject = "Ma xac nhan cua ban";
        String body = "Xin chào " + req.getUserName() + ",\n\n"
                + "Cảm ơn bạn đã đăng ký tài khoản EV Marketplace.\n"
                + "Mã xác minh (OTP) của bạn là: " + otp + "\n\n"
                + "Vui lòng nhập mã này trong vòng 10 phút để kích hoạt tài khoản.\n\n"
                + "Trân trọng,\n";
        emailVerifyService.sendEmailToUser(req.getUserEmail(),subject,body);
*/

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


    public void sendVerificationOtp(String email) {
        User user = userRepo.findByUserEmail(email);
        if (user == null)
            throw new RuntimeException("User not found");

        if (user.getUserStatus() == UserStatus.ACTIVE)
            throw new RuntimeException("Tài khoản đã được xác thực.");

        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setVerifiedCode(otp);
        userRepo.save(user);

        String title = "Mã xác thực tài khoản của bạn";
        String body = "Xin chào " + user.getUserName() + ",\n\n"
                + "Mã OTP của bạn là: " + otp + "\n\n"
                + "Vui lòng nhập mã này để xác minh email của bạn.\n\n"
                + "Trân trọng,\nĐội ngũ SWP391";

        emailVerifyService.sendEmailToUser(user.getUserEmail(), title, body);
    }



    public boolean verifyOtpCode(String email, String otp) {
        User user = userRepo.findByUserEmail(email);
        if (user == null) throw new RuntimeException("User not found");

        if (user.getVerifiedCode() == null || !user.getVerifiedCode().equals(otp)) {
            return false;
        }

        user.setVerifiedCode(null);
        user.setUserStatus(UserStatus.ACTIVE);
        userRepo.save(user);
        return true;
    }

    public List<User> findByUserCity(String city){
        List<User> u = userRepo.findByCityIgnoreCase(city);
        if (u == null) {
            throw new RuntimeException("User not found with city: " + city);
        }

        return u;

    }


    public void sendResetPasswordOtp(String email){
        User us = userRepo.findByUserEmail(email);
        if (us == null) {
            throw new RuntimeException("User not found with email: " + email);
        }

        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);
        us.setVerifiedCode(otp);
        userRepo.save(us);

        String subject = "Mã xác nhận đặt lại mật khẩu";
        String body = "Xin chào " + us.getUserName() + ",\n\n"
                + "Mã OTP của bạn là: " + otp + "\n"
                + "Mã này có hiệu lực trong vài phút.\n\n"
                + "EV Marketplace Team";
        emailVerifyService.sendEmailToUser(email, subject, body);
    }


    public boolean verifyResetOtp(String email, String otp) {
        User us = userRepo.findByUserEmail(email);
        if (us == null) {
            throw new RuntimeException("User not found with email: " + email);
        }

        if (us.getVerifiedCode() == null || !us.getVerifiedCode().equals(otp)) {
            return false;
        }

        us.setVerifiedCode(null);
        userRepo.save(us);
        return true;
    }


    public String verifyResetOtpAndGenerateToken(String email, String otp) {
        User us = userRepo.findByUserEmail(email);
        if (us == null) {
            throw new RuntimeException("User not found with email: " + email);
        }

        if (us.getVerifiedCode() == null || !us.getVerifiedCode().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        us.setVerifiedCode(null);
        userRepo.save(us);

        return jwtUtils.generateResetToken(email, 5); // 5 phút
    }

    public void resetUserPassword(String email, String password){
        User check = userRepo.findByUserEmail(email);
        if (check == null) {
            throw new RuntimeException("User not found with email: " + email);
        }

        check.setUserPassword(passwordEncoder.encode(password));
        userRepo.save(check);
    }

    public void sendEmailVerification(String email) {
        User user = userRepo.findByUserEmail(email);
        if (user == null) throw new RuntimeException("User not found");

        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);
        user.setVerifiedCode(otp);
        userRepo.save(user);

        String subject = "Mã xác thực tài khoản";
        String body = "Xin chào " + user.getUserName() + ",\n\n"
                + "Mã xác thực email của bạn là: " + otp + "\n"
                + "Vui lòng nhập mã này trong vòng 10 phút.\n\n"
                + "Trân trọng,\nEV Marketplace Team";
        emailVerifyService.sendEmailToUser(email, subject, body);
    }

}

