package com.healthtechbd.backend.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BkashCreateResponse {
    private String status;
    private String bkashURL;
    private String paymentId;

}
