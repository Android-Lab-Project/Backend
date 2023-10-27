package com.healthtechbd.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentSentDTO {
    private Long id;

    private String email;

    private Long doctorId;

    private String date;

    private String doctorName;

    private String doctorPic;
}
