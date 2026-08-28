package com.project.JobPortalSystem.Repository;

import com.project.JobPortalSystem.Entity.jobs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobsRepository extends JpaRepository<jobs, Long> {
    List<jobs> findByTitleContainingIgnoreCaseAndStatus(String title, String status);
    List<jobs> findByrecruiterId(Long recruiterId);
    List<jobs> findByTitleContainingIgnoreCaseAndRecruiterId(String title, Long recruiterId);
    Optional<jobs>findByIdAndStatus(Long jobId, String status);

    boolean existsByTitleIgnoreCaseAndCompanyIgnoreCaseAndRecruiterId(String title, String company, Long recruiterId);
}