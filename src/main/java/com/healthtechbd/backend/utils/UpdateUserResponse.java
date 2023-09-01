package com.healthtechbd.backend.utils;

import com.healthtechbd.backend.entity.AppUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class UpdateUserResponse extends RegistrationResponse{
    public UpdateUserResponse(ApiResponse apiResponse, AppUser appUser)
    {
        super(apiResponse,appUser);
    }
}
