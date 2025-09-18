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
    @GeneratedValue(strategy = GenerationType.UUID)
    private String listingId;
    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;
    @ManyToOne
    @JoinColumn(name = "Category", nullable = false)
    private Category category;
    @Column(name = "Title", nullable = false)
    private String title;
    @Column(name = "Description", nullable = false)
    private String description;
    @Column(name = "Price",nullable = false)
    private String price;
    @Column(name = "Contract_info",nullable = false)

    private String contractInfo;
    @Enumerated(EnumType.STRING)
    private ListingStatus status;
    @Column(name = "Create_At",nullable = false)

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "Update_At",nullable = false)

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;


}



