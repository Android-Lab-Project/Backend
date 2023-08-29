package com.healthtechbd.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosisReportDTO {

    private String description;

    private String reportURL;

    private String hospitalName;

    private Long hospitalId;

}
