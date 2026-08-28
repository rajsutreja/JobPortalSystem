package com.project.JobPortalSystem.DTO;

import lombok.Data;

import java.util.List;

@Data
public class ApplicationsResponse {
    private int count;
    private List<ApplicationDTO> applications;
}
