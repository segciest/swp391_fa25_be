package org.grp8.swp391.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle_listing {
    @OneToOne(cascade = CascadeType.ALL)
    @Id
    private Listing battery_id;
    @Column(name = "Brand",nullable = false)
    private String brand;
    @Column(name = "Capacity",nullable = false)

    private String capacity;
    @Column(name = "Cycle",nullable = false)

    private String cycle_count;
    @Column(name = "Warranty_info",nullable = false)

    private String warranty_info;
}
