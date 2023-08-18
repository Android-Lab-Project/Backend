package com.healthtechbd.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "expertise")
public class DoctorExpertise {
    private String expertise;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
