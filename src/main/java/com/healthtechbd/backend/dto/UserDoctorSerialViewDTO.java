package com.healthtechbd.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
