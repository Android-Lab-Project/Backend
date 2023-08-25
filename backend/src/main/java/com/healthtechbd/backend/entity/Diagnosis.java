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
@Table(name = "diagnosis")
public class Diagnosis {

    @ManyToOne
    @JoinColumn(name = "hospital_id", referencedColumnName = "id")
    Hospital hospital;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    LocalDate date;
    private String dp;
    private Long cost;

    @PrePersist
    public void setDefaultDp() {
        if (dp == null) {
            dp = "default.png";
        }
    }
}
