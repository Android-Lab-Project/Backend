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
@Table(name = "doctor_serials")
public class DoctorSerial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    private Long price;

    private LocalDate date;

    private LocalDate appointmentDate;

    private Double time;

    private Integer reviewChecked;

    @Column(length = 10000)
    private String prescription;

    private String paymentId;

    private String trxId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private AppUser user;

    @ManyToOne
    @JoinColumn(name = "doctor_id", referencedColumnName = "id")
    private AppUser doctor;

}
