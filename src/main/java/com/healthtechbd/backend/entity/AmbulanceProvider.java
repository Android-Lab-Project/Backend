package com.healthtechbd.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ambulance_provider")
public class AmbulanceProvider {
    public Long balance;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "appUser_id", referencedColumnName = "id")
    private AppUser appUser;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String bio;
    private String place;
}
