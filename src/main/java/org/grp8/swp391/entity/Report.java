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
public class
Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;
    @ManyToOne
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;
    @ManyToOne
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;
    @Column(name = "Reason", nullable = false,columnDefinition = "NVARCHAR(255)")
    private String reason;
    @Column(name = "Status", nullable = false)
    private ReportedStatus status;
    @Temporal(TemporalType.DATE)
    private Date createAt;

}
