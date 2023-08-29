package com.healthtechbd.backend.dto;

import com.healthtechbd.backend.entity.AppUser;
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

    private String place;

    private String degrees;

    private String dp;

    private List<String> days;
    private List<String> onlineDays;
    private List<Integer> times;
}
