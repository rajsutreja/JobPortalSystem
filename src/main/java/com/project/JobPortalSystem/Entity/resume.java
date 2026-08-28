package com.project.JobPortalSystem.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String filePath;

    private String fileType;

    @OneToOne
    @JoinColumn(name = "user_id")
    private users user;
}
