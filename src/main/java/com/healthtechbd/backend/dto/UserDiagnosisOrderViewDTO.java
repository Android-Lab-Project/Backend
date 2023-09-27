package com.healthtechbd.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDiagnosisOrderViewDTO {
    private Long id;

    private Long hospitalId;

    private String contactNo;

    private String deptContactNo;

    private String hospitalName;

    private String description;

    private LocalDate date;

    private String place;

    private String time;
}
