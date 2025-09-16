package org.grp8.swp391.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE  )
public class Contract {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long ContractID;
    String userID;
    @OneToOne
    @JoinColumn (name="transactionId",nullable=false)
    Transaction transactionId;
    @Temporal(TemporalType.TIMESTAMP)
    Date transactionDate;
    Boolean status;


}
