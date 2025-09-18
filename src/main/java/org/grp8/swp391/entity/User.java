package org.grp8.swp391.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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
    @Column(name = "Password", nullable = false)
    private String userPassword;
    @Column(name = "DoB", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dob;
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "Subscription_id", nullable = false)
    private Subscription subid;

    @Column(name = "Status", nullable = false)
    private String userStatus;
}
