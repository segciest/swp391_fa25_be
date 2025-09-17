package org.grp8.swp391.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Battery_listing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @OneToOne(cascade = CascadeType.ALL)
    private Listing battery_id;
    @Column(name = "Brand", nullable = false)

    private String brand;
    @Column(name = "Model", nullable = false)

    private String model;
    @Column(name = "Color", nullable = false)

    private String color;
    @Column(name = "Size", nullable = false)

    private String size;
    @Column(name = "Mileage", nullable = false)

    private String mileage;
    @Column(name = "Battery_capacity", nullable = false)

    private int battery_capacity;

}
