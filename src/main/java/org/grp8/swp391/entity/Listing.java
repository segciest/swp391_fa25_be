package org.grp8.swp391.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

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
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "listingId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images;

    @Column(name = "Title", nullable = false,columnDefinition = "NVARCHAR(255)")
    private String title;
    @Column(name = "Description", nullable = false,columnDefinition = "NVARCHAR(255)")
    private String description;
    @Column(name = "Brand", nullable = true)
    private String brand;
    @Column(name = "Warranty", nullable = true)

    private String warrantyInfo;
    @Column(name = "Model", nullable = true)

    private String model;
    @Column(name = "Year", nullable = true)

    private Integer year;
    @Column(name = "Seat", nullable = true)

    private Integer seats;
    @Column(name = "Vehicle_type", nullable = true,columnDefinition = "NVARCHAR(255)")

    private String vehicleType;
    @Column(name = "Color", nullable = true,columnDefinition = "NVARCHAR(255)")

    private String color;
    @Column(name = "Mileage", nullable = true)

    private String mileage;
    @Column(name = "Battery_capacity", nullable = true)

    private String batteryCapacity;
    @Column(name = "Capacity", nullable = true)

    private String capacity;
    @Column(name = "Voltage", nullable = true)

    private String voltage;
    @Column(name = "Cycle_count", nullable = true)

    private Integer cycleCount;
    @Column(name = "Battery_life", nullable = true)

    private String batteryLifeRemaining;

    @Column(name = "Price",nullable = false)
    private Double price;
    @Column(name = "Contact",nullable = false)
    private String contact;
    @Column(name = "City", columnDefinition = "NVARCHAR(100)")
    private String city;

    @Enumerated(EnumType.STRING)
    private ListingStatus status;
    @Column(name = "Create_At",nullable = false)

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "Update_At",nullable = true)

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;


}



