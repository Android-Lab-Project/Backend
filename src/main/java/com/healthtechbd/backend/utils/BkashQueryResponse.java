package com.healthtechbd.backend.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BkashQueryResponse {

    private String status;

    private String verificationStatus;

    private String transactionStatus;
}
