package com.healthtechbd.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "medicine_orders")
public class MedicineOrder {
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medicine_id", referencedColumnName = "id")
    Medicine medicine;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long count;

    private Long price;

}
