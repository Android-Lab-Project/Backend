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
    private String description;
    private Long price;
    private String contactNo;
}
