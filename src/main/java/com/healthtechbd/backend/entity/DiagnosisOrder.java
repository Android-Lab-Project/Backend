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
    private AppUser user;
    @ManyToOne
    @JoinColumn(name = "hospital_id", referencedColumnName = "id")
    private AppUser hospital;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private String place;
    private Long price;
    private String paymentId;
    private String trxId;
    private String reportURL;
    private LocalDate date;
    private LocalDate orderDate;
    private Double time;
    private Integer reviewChecked;
    private String deptContactNo;
    private Integer checked;
}
