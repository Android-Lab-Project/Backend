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
@Table(name = "medicine_orders")
public class MedicineOrder {


    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private AppUser user;
    @ManyToOne
    @JoinColumn(name = "pharmacy_id", referencedColumnName = "id")
    private AppUser pharmacy;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private Integer delivered;
    private String paymentId;
    private LocalDate date;
    private LocalDate orderDate;
    private String place;
    private String trxId;
    private Integer reviewChecked;
    private Long price;


}
