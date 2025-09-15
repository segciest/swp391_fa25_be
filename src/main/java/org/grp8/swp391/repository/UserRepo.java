package org.grp8.swp391.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.grp8.swp391.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepo extends JpaRepository<User, String> {
    User findByUserID(String userID);
    User findByUser_Name(String userName);
    User findByUser_Email(String userEmail);
    User findByUser_EmailAndUser_Password(String email, String password);
    User deleteByUserID(String userID);

    User save(User user);
    List<User> getAllUsers();

}
