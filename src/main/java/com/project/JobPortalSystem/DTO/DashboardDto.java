package com.project.JobPortalSystem.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardDto {
    private long totalUsers;
    private long totalRecruiters;
    private long totalJobSeekers;
    private long totalJobs;
    private long totalApplications;
}
