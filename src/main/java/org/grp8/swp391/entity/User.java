package org.grp8.swp391.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;



@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "Id", nullable = false)
    private String userID;
    @Column(name = "Name", nullable = false)
    private String user_Name;

    @Column(name = "Email", nullable = false)
    private String user_Email;
    @Column(name = "Password", nullable = false)
    private String user_Password;
    @Column(name = "Date of Birth", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dob;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false) // FK -> roles.role_id
    private Role role;

    @Column(name = "Status", nullable = false)
    private String user_Status;
}
