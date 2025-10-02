package org.grp8.swp391.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private String subscriptionName;
    private Date startDate;
    private Date endDate;
    private Double amount;
    private String method;
    private String transactionCode;
    private Date createDate;
    private String status;
}
