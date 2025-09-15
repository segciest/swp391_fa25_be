package org.grp8.swp391.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long review_ID;
    @ManyToOne
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;
    @ManyToOne
    @JoinColumn(name = "reviewer", nullable = false)
    private User reviewer;
    @ManyToOne
    @JoinColumn(name = "reviewd_user", nullable = false)
    private Review reviewed_user;
    private String comment;
}
