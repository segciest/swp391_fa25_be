package org.grp8.swp391.config;

import org.grp8.swp391.entity.User;
import org.grp8.swp391.repository.UserRepo;
import org.grp8.swp391.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class UserDetailService implements UserDetailsService {
    @Autowired
    private UserRepo us;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User u = us.findByUser_Email(email);

        if(u==null){
            throw new UsernameNotFoundException("User not found" + email);
        }

        return org.springframework.security.core.userdetails.User.withUsername(u.getUser_Email()).password(u.getUser_Password()).authorities(Collections.emptyList()).build();

    }
}
