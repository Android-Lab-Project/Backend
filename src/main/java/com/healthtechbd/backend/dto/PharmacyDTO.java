package com.healthtechbd.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyDTO extends UserDTO {
    private String place;
    private String bio;
    private Long balance;
    private Long rating;
    private Long reviewCount;

}
