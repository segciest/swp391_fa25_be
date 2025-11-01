package org.grp8.swp391.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VNPayPaymentRequest {
    private Long amount;              // Số tiền (VNĐ)
    private String orderInfo;         // Thông tin đơn hàng
    private Long subscriptionId;      // ID gói subscription
    private String userId;            // ID người dùng
}