package org.grp8.swp391.service;

import org.grp8.swp391.entity.Role;
import org.grp8.swp391.entity.User;
import org.grp8.swp391.entity.UserStatus;
import org.grp8.swp391.repository.RoleRepo;
import org.grp8.swp391.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class AuthUserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;


    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);


        String email = oAuth2User.getAttributes().get("email").toString();
        String name = oAuth2User.getAttributes().get("name").toString();

        User user = userRepo.findByUserEmail(email);
        if (user == null) {
            user = new User();
            user.setUserEmail(email);
            user.setUserName(name);
            user.setUserPassword("");
            user.setUserStatus(UserStatus.ACTIVE);

            Role role = roleRepo.findByRoleName("USER");
            user.setRole(role);

            userRepo.save(user);

        }
        return oAuth2User;
    }

}
