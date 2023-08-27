package com.healthtechbd.backend.dto;

import lombok.Data;

@Data
public class SignUpDTO {
    private String firstName;
    private String LastName;
    private String email;
    private String password;
    private String dp;
    private String contactNo;
}
