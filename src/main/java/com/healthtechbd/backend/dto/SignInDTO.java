package com.healthtechbd.backend.dto;

import lombok.Data;

@Data
public class SignInDTO {
    private Long id;
    private String email;
    private String password;
}
