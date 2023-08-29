package com.healthtechbd.backend.dto;

import com.healthtechbd.backend.entity.Ambulance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmbulanceProviderDTO extends UserDTO{
    private String bio;
    private String place;
}
