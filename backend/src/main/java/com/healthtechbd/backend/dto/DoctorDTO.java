package com.healthtechbd.backend.dto;

import com.healthtechbd.backend.entity.DoctorAvailableTime;
import com.healthtechbd.backend.entity.DoctorExpertise;
import com.healthtechbd.backend.entity.DoctorOnlineAvailableTime;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDTO {
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String contactNo;

    private String bio;
    private String currentHospital;
    private String place;
    private String degrees;

    List<DoctorExpertise> expertise;

    List<DoctorAvailableTime> availableTimes;

    List<DoctorOnlineAvailableTime> availableOnlineTimes;
}
