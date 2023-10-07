package com.healthtechbd.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JWTDTO {
    private String accessToken;
    private String email;
    private String contactNo;
    private String tokenType;
    private Long id;
    private String role;


}
