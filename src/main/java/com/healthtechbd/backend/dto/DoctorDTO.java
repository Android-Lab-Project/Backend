package com.healthtechbd.backend.dto;

import com.healthtechbd.backend.entity.DoctorAvailableTime;
import com.healthtechbd.backend.entity.DoctorOnlineAvailableTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDTO extends UserDTO {
    List<DoctorAvailableTime> availableTimes;
    List<DoctorOnlineAvailableTime> availableOnlineTimes;
    private String expertise;
    private String bio;
    private Long onlineFee;
    private Long offlineFee;
    private String currentHospital;
    private String place;
    private String degrees;
}
