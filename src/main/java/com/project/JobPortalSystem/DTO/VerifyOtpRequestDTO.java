package com.project.JobPortalSystem.DTO;

import lombok.Data;

@Data
public class VerifyOtpRequestDTO {
    private String email;
    private String otp;
}
