package com.healthtechbd.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="doctors")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="appUser_id", referencedColumnName = "id")
    AppUser appUser;

    @Column(nullable = false)
    private String bio;

    private String currentHospital;

    private String place;

    @ElementCollection
    @CollectionTable(name = "doctor_degress", joinColumns = @JoinColumn(name = "doctor_id"))
    @Column(name = "degrees")
    private List<String>degrees;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "doctor_expertise",
            joinColumns = @JoinColumn(name = "doctor_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "expertise_id", referencedColumnName = "id"))
    List<DoctorExpertise> expertise;

    @ElementCollection
    @CollectionTable(name = "doctor_available_days", joinColumns = @JoinColumn(name = "doctor_id"))
    @Column(name = "available_day")
    private Set<String> availableDays;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "doctor_time",
            joinColumns = @JoinColumn(name = "doctor_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "time_id", referencedColumnName = "id"))
    Set<DoctorAvailableTime>availableTimes;








}
