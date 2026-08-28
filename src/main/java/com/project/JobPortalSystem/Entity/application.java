package com.project.JobPortalSystem.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private users user;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private jobs job;


}
