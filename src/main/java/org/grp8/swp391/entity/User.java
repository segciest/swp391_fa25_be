package org.grp8.swp391.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "Id", nullable = false)
    private String userID;
    @NotBlank(message = "Tên người dùng không được để trống")

    @Column(name = "Name", nullable = false,columnDefinition = "NVARCHAR(100)")
    private String userName;

    @Column(name = "Email", nullable = false,unique = true)
    @Email
    @NotBlank(message = "Email không được để trống")
    private String userEmail;
    @JsonIgnore
    @Size(min = 5, max = 255, message = "Mật khẩu phải có ít nhất 6 ký tự")
    @Column(name = "Password", nullable = true)
    private String userPassword;

    @Column(name = "DoB", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date dob;
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = true)
    private Role role;
    @Column(name = "Phone", nullable = true, length = 10,unique = true)
    @Pattern( regexp = "^(0[3|5|7|8|9])[0-9]{8}$",message = "Invalid phone number")
    private String phone;

    @ManyToOne
    @JoinColumn(name = "Subscription_id")
    private Subscription subid;

    @Column(name = "Status", nullable = true)
    @Enumerated(EnumType.STRING)

    private UserStatus userStatus;
    @Column(name = "Address", nullable = true,columnDefinition = "NVARCHAR(255)")
    private String address;
    @Column(name = "City", nullable = true,columnDefinition = "NVARCHAR(255)")
    private String city;

    @Column(name = "Avatar_Url")
    private String avatarUrl;

    @Column(name = "Verified_code")
    private String verifiedCode;

    @JsonIgnore
    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Listing> listings;
}