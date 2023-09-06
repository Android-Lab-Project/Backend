package com.healthtechbd.backend.dto;

import lombok.Data;

@Data
public class JWTDTO {
    private String accessToken;
    private String tokenType = "Bearer";
    private long id;
    private String role;

    public JWTDTO(String accessToken, long id, String role) {
        this.accessToken = accessToken;
        this.id = id;
        this.role = role;
    }
}
