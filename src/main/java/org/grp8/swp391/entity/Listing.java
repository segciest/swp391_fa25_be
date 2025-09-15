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
public class Listing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long listing_id;
    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;
    private String categories;
    private String title;
    private String description;
    private String price;
    private String status;
    @Temporal(TemporalType.TIMESTAMP)

    private Date created_at;


}
