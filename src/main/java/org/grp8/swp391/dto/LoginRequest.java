package org.grp8.swp391.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    private String userEmail;
    private String userPassword;

}
