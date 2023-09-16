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

    private Long _7DaysUserCount;
    private Long _30DaysUserCount;
    private Long totalUserCount;

    private Long totalDoctorSerialCount;
    private Long totalDiagnosisOrderCount;
    private Long totalMedicineOrderCount;
    private Long totalAmbulanceTripCount;

    private Long _7DaysIncome;

    private Long _30DaysIncome;

    private Long totalIncome;

    private List<LocalDate> dates;

    private List<Long> incomes;
}
