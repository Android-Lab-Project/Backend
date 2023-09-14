package com.healthtechbd.backend.dto;

import lombok.Data;

@Data
public class JWTDTO {
    private String accessToken;
    private String tokenType = "Bearer";
    private Long id;
    private String role;

    public JWTDTO(String accessToken, Long id, String role) {
        this.accessToken = accessToken;
        this.id = id;
        this.role = role;
    }
}
