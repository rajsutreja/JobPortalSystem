package com.project.JobPortalSystem.Repository;

import com.project.JobPortalSystem.Entity.resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResumeRepository extends JpaRepository<resume, Long> {
    Optional<resume> findByUser_Id(Long userId);
}