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
public class User_Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "User_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "Sub_id", nullable = false)
    private Subscription subscriptionId;
    private Date startDate;
    private Date endDate;
    private String status;
}
