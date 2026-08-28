package com.project.JobPortalSystem.DTO;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String roles;
    private String status;
}
