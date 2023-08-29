package com.healthtechbd.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HospitalDTO extends UserDTO{
    private String hospitalName;
    private String place;
    private String bio;
}
