package org.grp8.swp391.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.grp8.swp391.entity.Role;
import org.grp8.swp391.entity.Subscription;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String userId;

    private String userName;
    private String userEmail;
    private String phone;
    private Date dob;
    private String role;
    private String subName;
    private String userStatus;
    private String token;
}
