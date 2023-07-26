package com.healthtechbd.backend.dto;

import lombok.Data;

@Data
public class JWTDTO {
    private String accessToken;
    private String tokenType ="Bearer";

    public JWTDTO(String accessToken) {
        this.accessToken = accessToken;
    }
}
