package com.healthtechbd.backend.entity;

public enum RoleType {
    USER("USER"),
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
