package org.grp8.swp391.service;

import org.grp8.swp391.entity.User;
import org.grp8.swp391.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    public User login(String email, String password) {
        User user = userRepo.findByUserEmailAndUserPassword(email, password);
        if (user == null) {
            throw new RuntimeException("Invalid email or password");
        }
        return user;
    }

    public User findByUser_Email(String email){


        return userRepo.findByUserEmail(email);
    }

    public void deleteById(String id){
        User check = userRepo.findByUserID(id);
        userRepo.delete(check);
    }

    public User save(User user){
        return userRepo.save(user);
    }

    public User updateUser(User up, String id){
        User check = userRepo.findByUserID(id);
        if (check == null) {
            throw new RuntimeException("User not found with id: " + id);
        }

        if (up.getUserName() != null) {
            check.setUserName(up.getUserName());
        }
        if (up.getUserEmail() != null) {
            check.setUserEmail(up.getUserEmail());
        }
        if (up.getUserPassword() != null) {
            check.setUserPassword(up.getUserPassword());
        }
        if (up.getDob() != null) {
            check.setDob(up.getDob());
        }
        if (up.getUserStatus() != null) {
            check.setUserStatus(up.getUserStatus());
        }
        if (up.getRole() != null) {
            check.setRole(up.getRole());
        }

        return userRepo.save(check);
    }

    public User registerUser(User user){
        if(userRepo.findByUserEmail(user.getUserEmail())!=null){
            throw new RuntimeException("User already exists");
        }

        if(user.getUserStatus()==null){
            user.setUserStatus("Active");
        }

        return userRepo.save(user);

    }

    public List<User> getAllUsers(){
        return userRepo.findAll();
    }

    public User findUserById(String id){
        return userRepo.findByUserID(id);
    }
}
