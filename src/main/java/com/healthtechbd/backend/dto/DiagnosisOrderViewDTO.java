package com.healthtechbd.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiagnosisOrderViewDTO {

    private Long id;

    private Long patientId;

    private String contanctNo;

    private String patientName;

    private String description;

    private String place;

    private LocalDate orderDate;

    private Double time;

}
