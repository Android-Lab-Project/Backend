package com.healthtechbd.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="doctor_online_availableTime")
public class DoctorOnlineAvailableTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String day;

    private LocalDate date;

    private Integer onlineCount;

    private Double onlineAvailTime;

    private Integer onlineStartTime;

    private Integer onlineEndTime;

}