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
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @OneToOne
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "transaction_date", nullable = false)
    private Date transactionDate;

    @Column(name = "status", nullable = false)
    private String status;

}
