package com.healthtechbd.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatisticsDTO {

    private Long _7daysUserCount;
    private Long _30daysUserCount;
    private Long totalUserCount;

    private Long totalDoctorSerialCount;
    private Long totalDiagnosisOrderCount;
    private Long totalMedicineOrderCount;
    private Long totalAmbulanceTripCount;

    private Long _7daysIncome;

    private Long _30daysIncome;

    private Long totalIncome;

    private List<LocalDate> dates;

    private List<Long> incomes;
}
