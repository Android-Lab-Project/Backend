package com.healthtechbd.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmbulanceDTO {
    private Long id;
    private String name;
    private String type;
    private Long bookingCost;
    private Long appUser_id;
}
