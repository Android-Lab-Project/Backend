package com.healthtechbd.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "doctors")
public class Doctor {

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "appUser_id", referencedColumnName = "id")
    AppUser appUser;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "doctor_expertise",
            joinColumns = @JoinColumn(name = "doctor_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "expertise_id", referencedColumnName = "id"))
    List<DoctorExpertise> expertise;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "relation_doctor_offlineTime",
            joinColumns = @JoinColumn(name = "doctor_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "time_id", referencedColumnName = "id"))
    List<DoctorAvailableTime> availableTimes;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "relation_doctor_onlineTime",
            joinColumns = @JoinColumn(name = "doctor_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "onlineTime_id", referencedColumnName = "id"))
    List<DoctorOnlineAvailableTime> availableOnlineTimes;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String bio;
    private String currentHospital;
    private String place;
    private String degrees;


}
