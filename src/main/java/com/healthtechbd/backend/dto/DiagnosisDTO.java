package com.healthtechbd.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiagnosisDTO {

    private Long id;
    private String name;
    private String hospitalName;
    private String type;
    private String description;
    private String place;
    private Long price;
    private String startTime;
    private String endTime;
    private String deptContactNo;
    private Long hospitalId;
    private Double rating;
}
