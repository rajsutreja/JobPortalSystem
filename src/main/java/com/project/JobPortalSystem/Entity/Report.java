package com.project.JobPortalSystem.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String reportType; // JOB or USER

    @Column(nullable = false)
    private String reason;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, RESOLVED, REJECTED

    @ManyToOne
    private users reportedByUser;

    @ManyToOne
    private jobs reportedToJob;

    @ManyToOne
    private users reportedToUser;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = "PENDING";
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
