package com.healthtechbd.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDiagnosisOrderViewDTO {
    private Long id;

    private String hospitalName;

    private String description;

    private String place;

    private Double time;
}
