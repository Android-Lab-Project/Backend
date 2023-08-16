package com.healthtechbd.backend.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;


@Getter
@Setter
public class ApiException extends RuntimeException {
    private HttpStatus status;
    private String message;

    public ApiException(HttpStatus status, String message)
    {
        this.status=status;
        this.message = message;
    }

    public ApiException(HttpStatus status, String message1, String message2) {
        super(message1);
        this.status = status;
        this.message = message2;
    }

    public ApiException(String message) {
        this.message = message;
    }


}
