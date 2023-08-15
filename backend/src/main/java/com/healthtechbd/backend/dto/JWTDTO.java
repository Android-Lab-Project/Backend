package com.healthtechbd.backend.dto;

import lombok.Data;

@Data
public class JWTDTO {
    private String accessToken;
    private String tokenType ="Bearer";
    private long id;

    public JWTDTO(String accessToken, long id) {
        this.accessToken = accessToken;
        this.id = id;
    }
}
