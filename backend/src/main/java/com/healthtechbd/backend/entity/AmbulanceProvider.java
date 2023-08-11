package com.healthtechbd.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="ambulance_provider")
public class AmbulanceProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="appUser_id", referencedColumnName = "id")
    AppUser appUser;

    private String bio;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "provider_ambulance",
            joinColumns = @JoinColumn(name = "provider_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "ambulance_id", referencedColumnName = "id"))
    private List<Ambulance> ambulances;
}
