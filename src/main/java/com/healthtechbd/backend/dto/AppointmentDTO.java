package com.healthtechbd.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AppointmentDTO {
    private String email;
    private Long doctorId;
}
