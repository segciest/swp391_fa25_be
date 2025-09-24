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
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;
    @ManyToOne
    @JoinColumn(name = "UserSub_id", nullable = false)
    private User_Subscription userSubscription;
    @Column(name = "Amount", nullable = false)
    private Double amount;
    @Column(name = "Method", nullable = false)

    private String method;
    @Column(name = "Trans_Code", nullable = false, unique = true)

    private String transactionCode;
    @Column(name = "CreateDate", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;


}
