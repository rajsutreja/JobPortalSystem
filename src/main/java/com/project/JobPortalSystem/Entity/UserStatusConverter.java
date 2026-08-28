package com.project.JobPortalSystem.Entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserStatusConverter implements AttributeConverter<UserStatus, String> {

    @Override
    public String convertToDatabaseColumn(UserStatus status) {
        return status == null ? UserStatus.ACTIVE.name() : status.name();
    }

    @Override
    public UserStatus convertToEntityAttribute(String value) {
        return UserStatus.fromValue(value);
    }
}
