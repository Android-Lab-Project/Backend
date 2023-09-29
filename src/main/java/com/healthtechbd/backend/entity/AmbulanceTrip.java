package com.healthtechbd.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ambulance_trips")
public class AmbulanceTrip {
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private AppUser user;
    @ManyToMany
    @JoinTable(name = "ambulancetrip_bidder",
            joinColumns = @JoinColumn(name = "trip_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "ambulanceProvider_id", referencedColumnName = "id"))
    private List<AppUser> bidders;
    @ManyToOne
    @JoinColumn(name = "ambulanceProvider_id", referencedColumnName = "id")
    private AppUser ambulanceProvider;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String source;
    private String destination;
    private String paymentId;
    private String trxId;
    private Long price;
    private LocalDate orderDate;
    private LocalDate date;
    private String location;
    private Integer reviewChecked;
    private Integer checked;

}
