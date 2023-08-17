package com.healthtechbd.backend.utils;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ApiResponse {
    private String type;
    private String message;

    private ApiResponse(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public static ApiResponse create(String type, String message) {
        return new ApiResponse(type, message);
    }

    public boolean empty() {
        return type == null && message == null;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
