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
        User user = userRepo.findByUser_EmailAndUser_Password(email, password);
        if (user == null) {
            throw new RuntimeException("Invalid email or password");
        }
        return user;
    }

    public User findByUser_Email(String email){


        return userRepo.findByUser_Email(email);
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

        if (up.getUser_Name() != null) {
            check.setUser_Name(up.getUser_Name());
        }
        if (up.getUser_Email() != null) {
            check.setUser_Email(up.getUser_Email());
        }
        if (up.getUser_Password() != null) {
            check.setUser_Password(up.getUser_Password());
        }
        if (up.getDob() != null) {
            check.setDob(up.getDob());
        }
        if (up.getUser_Status() != null) {
            check.setUser_Status(up.getUser_Status());
        }
        if (up.getRole() != null) {
            check.setRole(up.getRole());
        }

        return userRepo.save(check);
    }

    public User registerUser(User user){
        if(userRepo.findByUser_Email(user.getUser_Email())!=null){
            throw new RuntimeException("User already exists");
        }

        if(user.getUser_Status()==null){
            user.setUser_Status("Active");
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
