package com.project.JobPortalSystem.DTO;

import lombok.Data;

@Data
public class ReportRequest {
    private String reportType; // JOB or USER
    private Long targetId;
    private String reason;
    private String description;
}
