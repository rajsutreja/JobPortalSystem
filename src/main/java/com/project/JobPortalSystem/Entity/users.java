package com.project.JobPortalSystem.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private UserRole role;

    private UserStatus status = UserStatus.ACTIVE;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = UserStatus.ACTIVE;
        }
    }

}
