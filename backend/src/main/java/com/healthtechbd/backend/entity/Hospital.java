package com.healthtechbd.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="hospital")
public class Hospital {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="appUser_id", referencedColumnName = "id")
    AppUser appUser;

    @Column(nullable = false)
    private String hospitalName;

    @Column(nullable = false)
    private String bio;

    @Column(nullable = false)
    private String place;
}