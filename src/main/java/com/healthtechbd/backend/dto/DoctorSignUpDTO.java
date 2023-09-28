package com.healthtechbd.backend.dto;

import com.healthtechbd.backend.entity.AppUser;
import com.healthtechbd.backend.entity.DoctorAvailableTime;
import com.healthtechbd.backend.entity.DoctorOnlineAvailableTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorSignUpDTO {
    private AppUser appUser;
    private List<DoctorAvailableTime> availableTimes;
    private List<DoctorOnlineAvailableTime> availableOnlineTimes;
    private String bio;
    private String currentHospital;
    private String expertise;
    private String place;
    private String degrees;
    private Long onlineFee;
    private Long offlineFee;
}
