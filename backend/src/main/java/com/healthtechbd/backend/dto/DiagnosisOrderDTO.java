package com.healthtechbd.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosisOrderDTO {
    private String description;
    private Long price;
    private String reportURL;
    private Long hospitalId;
    private Double time;
}
