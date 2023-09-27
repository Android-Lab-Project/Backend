package com.healthtechbd.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorSerialViewDTO {
    private Long id;

    private Long patientId;

    private String contactNo;

    private String patientName;

    private String type;

    private Double time;

    private LocalDate date;
}
