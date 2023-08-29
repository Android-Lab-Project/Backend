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
@Table(name = "diagnosis_orders")
public class DiagnosisOrder {

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    AppUser user;
    @ManyToOne
    @JoinColumn(name = "hospital_id", referencedColumnName = "id")
    AppUser hospital;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private String place;
    private Long price;
    private String reportURL;
    private LocalDate date;
    private Double time;
}