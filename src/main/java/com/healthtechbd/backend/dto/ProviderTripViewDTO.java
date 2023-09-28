package com.healthtechbd.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderTripViewDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String userContactNo;
    private String source;
    private String destination;
    private Long price;
    private String location;
    private LocalDate orderDate;
}
