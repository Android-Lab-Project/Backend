package com.healthtechbd.backend.dto;

import com.healthtechbd.backend.entity.Diagnosis;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HospitalDTO extends UserDTO{
    private String hospitalName;
    private String place;
    private String bio;
}
