package com.healthtechbd.backend.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


public enum RoleType {
    USER("User"),
    ADMIN("ADMIN"),
    DOCTOR("DOCTOR"),
    PATIENT("PATIENT"),
    COMPANY("COMPANY");



    private final String value;

    RoleType(String value)
    {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
