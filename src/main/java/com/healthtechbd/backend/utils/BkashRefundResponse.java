package com.healthtechbd.backend.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BkashRefundResponse {
    private String status;
    private String refundTrxID;
    private String transactionStatus;
}
