package com.healthtechbd.backend.utils;

import com.healthtechbd.backend.entity.AppUser;

public class RegistrationResponse {
    private final ApiResponse response;
    private final AppUser user;

    public RegistrationResponse(ApiResponse response, AppUser user) {
        this.response = response;
        this.user = user;
    }

    public RegistrationResponse()
    {
        this.response=null;
        this.user =null;
    }

    public ApiResponse getResponse() {
        return response;
    }

    public AppUser getUser() {
        return user;
    }
}

