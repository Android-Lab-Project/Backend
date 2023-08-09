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
@Table(name="doctors",uniqueConstraints = {@UniqueConstraint(columnNames={"email"})})

public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String contactNo;

    @Column(nullable = false)
    private String bio;


    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "doctor_expertise",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "expertise_id", referencedColumnName = "id"))
    List<DoctorExpertise> expertise;

    @ElementCollection
    @CollectionTable(name = "doctor_available_days", joinColumns = @JoinColumn(name = "doctor_id"))
    @Column(name = "available_day")
    private Set<String> availableDays;

    @OneToMany(mappedBy = "doctor")
    Set<DoctorAvailableTime>availableTimes;








}
