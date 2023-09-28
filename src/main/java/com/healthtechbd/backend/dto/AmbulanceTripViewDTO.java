package com.healthtechbd.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmbulanceTripViewDTO {
    private Long id;
    private Long userId;
    private Long providerId;
    private String userName;
    private String userContactNo;
    private String providerName;
    private String providerContactNo;
    private String source;
    private String destination;
    private String location;
    private Long price;
    private LocalDate orderDate;
}
