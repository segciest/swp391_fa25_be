package org.grp8.swp391.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.grp8.swp391.entity.Role;
import org.grp8.swp391.entity.UserStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {

        private String userId;
        private String userName;
        private String userEmail;
        private String phone;
        private String roleName;
        private UserStatus status;

}
