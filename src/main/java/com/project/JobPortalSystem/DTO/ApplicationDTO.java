package com.project.JobPortalSystem.DTO;

import lombok.Data;

@Data
public class ApplicationDTO {
    private Long applicationId;
    private String status;
    private Long userId;
    private String userName;
    private String userEmail;
    private Boolean resumeUploaded;
}
