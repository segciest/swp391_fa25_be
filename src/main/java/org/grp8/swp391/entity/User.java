package org.grp8.swp391.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;



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
    @Column(name = "Name", nullable = false,columnDefinition = "NVARCHAR(100)")
    private String userName;

    @Column(name = "Email", nullable = false)
    @Email
    private String userEmail;
    @Column(name = "PasswordEncode", nullable = false)
    private String userPassword;
    @Column(name = "DoB", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dob;
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "Phone", nullable = false, length = 10)
    @Pattern(
            regexp = "^(0[3|5|7|8|9])[0-9]{8}$",
            message = "Invalid phone number"
    )
    private String phone;

    @ManyToOne
    @JoinColumn(name = "Subscription_id")
    private Subscription subid;

    @Column(name = "Status", nullable = false)
    @Enumerated(EnumType.STRING)

    private UserStatus userStatus;
}