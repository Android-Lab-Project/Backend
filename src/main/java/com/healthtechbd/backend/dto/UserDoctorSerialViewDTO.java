package com.healthtechbd.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDoctorSerialViewDTO {
    private Long id;
    private String type;
    private Long doctorId;
    private String doctorName;
    private String contactNo;
    private Double time;
    private LocalDate date;
}
