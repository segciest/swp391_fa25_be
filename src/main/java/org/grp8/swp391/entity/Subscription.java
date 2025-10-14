package org.grp8.swp391.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_id")
    private Long subId;

    @Column(name = "sub_name", nullable = false,columnDefinition = "NVARCHAR(255)")
    private String subName;

    @Column(name = "sub_detail", nullable = false,columnDefinition = "NVARCHAR(255)")
    private String subDetails;

    @Column(name = "sub_price", nullable = false)
    private String subPrice;

    @Column(name = "duration", nullable = false)
    private int duration;
    @Column(name = "priority_level", nullable = false)
    private int priorityLevel;

    @Column(name = "status")
    private String status;

}
