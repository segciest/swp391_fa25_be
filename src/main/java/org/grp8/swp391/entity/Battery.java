package org.grp8.swp391.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Battery {
    @Id
    private String battery_id;
    private String battery_name;
    private String battery_cycle;
    private String battery_status;
    private String capacity;
    private String warranty_info;
    @OneToOne
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
}
