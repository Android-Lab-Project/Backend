package com.healthtechbd.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosisOrderDTO {
    private String description;
    private Long price;
    private String place;
    private String reportURL;
    private Long hospitalId;
    private LocalDate orderDate;
    private Double time;
}
