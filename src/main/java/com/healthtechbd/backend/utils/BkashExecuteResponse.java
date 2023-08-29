package com.healthtechbd.backend.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BkashExecuteResponse {
    private String status;
    private String transactionStatus;
    private String trxID;
}
