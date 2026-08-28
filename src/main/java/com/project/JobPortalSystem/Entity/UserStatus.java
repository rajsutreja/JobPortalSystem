package com.project.JobPortalSystem.Entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum UserStatus {
    ACTIVE,
    BLOCKED;

    @JsonCreator
    public static UserStatus fromValue(String value) {
        if (value == null || value.isBlank()) {
            return ACTIVE;
        }

        String normalized = value.trim()
                .replace("-", "_")
                .replace(" ", "_")
                .toUpperCase();

        return UserStatus.valueOf(normalized);
    }
}
