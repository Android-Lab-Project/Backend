package com.healthtechbd.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDTO {


    private Long _7DaysCount;

    private Long _30DaysCount;

    private Long totalCount;

    private Long _7DaysIncome;

    private Long _30DaysIncome;

    private Long totalIncome;

    private List<LocalDate> dates;

    private List<Long> incomes;
}
