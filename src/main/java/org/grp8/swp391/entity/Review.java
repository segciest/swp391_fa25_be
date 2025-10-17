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
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne
    @JoinColumn(name = "reviewer", nullable = false)
    private User reviewer;
    @ManyToOne
    @JoinColumn(name = "reviewd_user", nullable = false)
    private User reviewedUser;
    @Column(columnDefinition = "NVARCHAR(255)")
    private String comment;
    private int rate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;
}
