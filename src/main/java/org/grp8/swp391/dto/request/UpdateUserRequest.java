package org.grp8.swp391.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    private String userName;
    private String userEmail;
    private String phone;
    private String password;
    private Date dob;

}
