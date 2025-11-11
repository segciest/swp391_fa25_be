package org.grp8.swp391.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank
    private String userName;
    @NotBlank
    @Email
    private String userEmail;
    @NotBlank
    @Size(min = 5, max = 255, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String userPassword;
    @NotBlank
    @Pattern( regexp = "^(0[3|5|7|8|9])[0-9]{8}$",message = "Invalid phone number")
    private String phone;
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Past(message = "Ngày sinh phải ở trong quá khứ")
    private Date dob;
    private Long subId;
    private String city;
}
