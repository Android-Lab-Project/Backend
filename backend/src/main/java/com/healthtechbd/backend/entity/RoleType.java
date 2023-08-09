package com.healthtechbd.backend.entity;

public enum RoleType {
    USER("USER"),
    ADMIN("ADMIN"),
    DOCTOR("DOCTOR"),
    HOSPITAL("HOSPITAL"),
    AMBULANCE("AMBULANCE"),
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

