package com.healthtechbd.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "medicines")
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "medicine_name")
    private String name;

    @Column(name = "manufacturer_name")
    private String company;

    @Column(name = "generic_name")
    private String generic_name;

    @Column(name = "price")
    private Double price;

}
