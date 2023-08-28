package com.healthtechbd.backend.dto;

import com.healthtechbd.backend.entity.Role;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class AppUserDetailsDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String dp;
    private String contactNo;
    private List<Role> roles;
    private JWTDTO jwtdto;

}
