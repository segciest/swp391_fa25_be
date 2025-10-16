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
    User findByUserName(String userName);
    User findByUserEmail(String userEmail);
    User findByUserEmailAndUserPassword(String userEmail, String userPassword);
    void deleteByUserID(String userID);
    User findByPhone(String phone);
    User findByCity(String city);

    User findByVerifiedCode(String code);


}
