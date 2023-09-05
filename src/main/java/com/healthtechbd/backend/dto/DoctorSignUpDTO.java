package com.healthtechbd.backend.dto;

import com.healthtechbd.backend.entity.AppUser;
import com.healthtechbd.backend.entity.DoctorAvailableTime;
import com.healthtechbd.backend.entity.DoctorOnlineAvailableTime;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorSignUpDTO {
    AppUser appUser;

    @Column(nullable = false)
    private String bio;

    private String currentHospital;

    private String expertise;

    private String place;

    private String degrees;

    private Long onlineFee;

    private Long offlineFee;

    List<DoctorAvailableTime>availableTimes;
    List<DoctorOnlineAvailableTime>availableOnlineTimes;
}
