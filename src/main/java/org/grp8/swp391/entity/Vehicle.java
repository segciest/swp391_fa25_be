package org.grp8.swp391.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    @Id

    private String vehicle_id;
    private String vehicle_brand;
    private String vehicle_model;
    private String vehicle_year;
    private String vehicle_condition;
    private String mileage_km;
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    @OneToOne
    @JoinColumn(name = "listing_id", nullable = false)

    private Listing listing_id;

}
