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
    private AmbulanceProvider ambulanceProvider;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String type;
    private String dp;

    @PrePersist
    public void setDefaultDp() {
        if (dp == null) {
            dp = "default.png";
        }
    }
}
