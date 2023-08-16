package com.healthtechbd.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="doctor_availableTime")
public class DoctorAvailableTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String day;

    private LocalDate date;

    private Integer count;

    private Integer startTime;
    private Integer endTime;
    private Integer onlineStartTime;
    private Integer onlineEndTime;

}
