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


    LocalDate date;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    AppUser user;
    @ManyToOne
    @JoinColumn(name = "pharmacy_id", referencedColumnName = "id")
    AppUser pharmacy;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private Integer delivered;
    private Long price;


}
