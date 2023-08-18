package com.healthtechbd.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ambulance")
public class Ambulance {
    @ManyToOne
    @JoinColumn(name = "ambulanceProvider_id", referencedColumnName = "id")
    AmbulanceProvider ambulanceProvider;
    @ManyToOne
    @JoinColumn(name = "appUser_id", referencedColumnName = "id")
    AppUser appUser;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String type;
    private Long bookingCost;
}
