package com.project.JobPortalSystem.Entity;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
public class jobs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String company;

    private String location;

    private Double salary;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private String status; // OPEN, CLOSED, ARCHIVED

    @ManyToOne
    @JoinColumn(name = "recruiter_id")
    private users recruiter;
}
