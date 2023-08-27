package com.healthtechbd.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ambulance_trips")
public class AmbulanceTrip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String source;
    private String destination;
    private Long price;
    private LocalDate date;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    AppUser user;
    @ManyToOne
    @JoinColumn(name = "ambulanceProvider_id", referencedColumnName = "id")
    AppUser ambulanceProvider;
}
