package com.project.JobPortalSystem.DTO;

import lombok.Data;

@Data
public class JobDto {
    private Long id;
    private String title;
    private String company;
    private String location;
    private Double salary;
    private String description;
    private String recruiterName;
    private String status;
}
