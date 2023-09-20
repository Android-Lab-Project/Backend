package com.healthtechbd.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicineOrderViewDTO {
    private Long id;
    private Long userId;
    private Long pharmacyId;
    private String userName;
    private String pharmacyName;
    private String userEmail;
    private String pharmacyEmail;
    private String description;
    private Long price;
    private String place;
    private String contactNo;
    private Long distance;
}
