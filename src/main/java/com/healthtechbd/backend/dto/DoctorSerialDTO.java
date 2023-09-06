package com.healthtechbd.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorSerialDTO {
    private String type;

    private Long price;

    private LocalDate appointmentDate;

    private String prescription;

    private Double time;

    private Long doctor_id;
}
