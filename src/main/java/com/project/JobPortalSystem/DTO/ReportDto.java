package com.project.JobPortalSystem.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReportDto {
    private Long id;
    private String reportType;
    private String reason;
    private String description;
    private String status;
    private Long reportedByUserId;
    private String reportedByUserName;
    private Long reportedToJobId;
    private String reportedToJobTitle;
    private Long reportedToUserId;
    private String reportedToUserName;
    private LocalDateTime createdAt;
}
