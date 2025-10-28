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
@Table(name = "Payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;
    
    @ManyToOne
    @JoinColumn(name = "UserSub_id", nullable = false)
    private User_Subscription userSubscription;
    
    @Column(name = "User_id", nullable = false)
    private String userId;
    
    @Column(name = "Amount", nullable = false)
    private Double amount;
    
    @Column(name = "Method", nullable = false)
    private String method; // "VNPAY", "MOMO", "CARD", etc.
    
    @Column(name = "Trans_Code", nullable = true, unique = true)
    private String transactionCode; // VNPay transaction number
    
    @Column(name = "Order_Id", nullable = false, unique = true)
    private String orderId; // Order reference ID
    
    @Column(name = "Order_Info", nullable = true)
    private String orderInfo; // Description
    
    @Column(name = "Bank_Code", nullable = true)
    private String bankCode; // NCB, VIETCOMBANK, etc.
    
    @Column(name = "Response_Code", nullable = true)
    private String responseCode; // 00 = success, 24 = cancel, etc.
    
    @Column(name = "CreateDate", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;
    
    @Column(name = "UpdatedAt", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false)
    private PaymentStatus status; // PENDING, COMPLETED, FAILED, CANCELLED
    
    @Column(name = "Provider_Response", columnDefinition = "TEXT")
    private String providerResponse; // Raw JSON response from provider
}
