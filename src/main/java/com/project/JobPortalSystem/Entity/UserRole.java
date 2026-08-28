package com.project.JobPortalSystem.Entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum UserRole {
    JOB_SEEKER,
    RECRUITER,
    ADMIN;

    @JsonCreator
    public static UserRole fromValue(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim()
                .replace("-", "_")
                .replace(" ", "_")
                .toUpperCase();


        for (UserRole role : values()) {
            if (role.name().equals(normalized)) {
                return role;
            }
        }

        throw new IllegalArgumentException("Invalid user role: " + value);
    }
}
