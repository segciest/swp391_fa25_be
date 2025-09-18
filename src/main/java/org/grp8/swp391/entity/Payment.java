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
    private User_Subscription userId;
    @Column(name = "Amount", nullable = false)
    private String amount;
    @Column(name = "Method", nullable = false)

    private String method;
    @Column(name = "Trans_Code", nullable = false)

    private String transactionCode;
    @Column(name = "CreateDate", nullable = false)

    public Date createDate;
}
