package com.project.JobPortalSystem.Repository;

import com.project.JobPortalSystem.Entity.Report;
import com.project.JobPortalSystem.Entity.users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByStatus(String status);
    List<Report> findByReportType(String reportType);
    boolean existsByReportedByUser_IdAndReportTypeAndReportedToJob_IdAndStatus(Long reportedById, String reportType, Long reportedJobId, String status);
    boolean existsByReportedByUser_IdAndReportTypeAndReportedToUser_IdAndStatus(Long reportedById, String reportType, Long reportedUserId, String status);
    List<Report> findByReportedByUser_Email(String email);
}
