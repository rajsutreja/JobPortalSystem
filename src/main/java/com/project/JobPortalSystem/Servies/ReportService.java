package com.project.JobPortalSystem.Servies;

import com.project.JobPortalSystem.DTO.ReportDto;
import com.project.JobPortalSystem.DTO.ReportRequest;
import com.project.JobPortalSystem.Entity.Report;
import com.project.JobPortalSystem.Entity.jobs;
import com.project.JobPortalSystem.Entity.users;
import com.project.JobPortalSystem.Repository.JobsRepository;
import com.project.JobPortalSystem.Repository.ReportRepository;
import com.project.JobPortalSystem.Repository.UserRepository;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReportService {
    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobsRepository jobsRepository;


    public ReportDto createReport(ReportRequest request, String reporterEmail) {
        if (request.getReportType() == null || request.getReportType().isBlank()) {
            throw new IllegalArgumentException("Report type is required");
        }
        if (request.getTargetId() == null) {
            throw new IllegalArgumentException("Target id is required");
        }
        if (request.getReason() == null || request.getReason().isBlank()) {
            throw new IllegalArgumentException("Reason is required");
        }

        users reporter = userRepository.findByEmail(reporterEmail)
                .orElseThrow(() -> new RuntimeException("Reporter not found"));

        Report report = new Report();
        String reportType = request.getReportType().trim().toUpperCase();
        report.setReportType(reportType);
        report.setReason(request.getReason());
        report.setDescription(request.getDescription());
        report.setReportedByUser(reporter);


        if ("JOB".equals(reportType)) {
            jobs job = jobsRepository.findById(request.getTargetId())
                    .orElseThrow(() -> new RuntimeException("Job not found"));
            if (reportRepository.existsByReportedByUser_IdAndReportTypeAndReportedToJob_IdAndStatus(
                    reporter.getId(), reportType, job.getId(), "PENDING")) {
                throw new IllegalArgumentException("You already reported this job ,still in working");
            }
            report.setReportedToJob(job);
        } else if ("USER".equals(reportType)) {
            users reportedUser = userRepository.findById(request.getTargetId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            if (reportRepository.existsByReportedByUser_IdAndReportTypeAndReportedToUser_IdAndStatus(
                    reporter.getId(), reportType, reportedUser.getId(), "PENDING")) {
                throw new IllegalArgumentException("You already reported this user, still in working");
            }
            report.setReportedToUser(reportedUser);
        } else {
            throw new IllegalArgumentException("Invalid report type. Use JOB or USER");
        }

        return toReportDto(reportRepository.save(report));
    }

    public List<ReportDto> getAllReports() {
        List<Report> reports = reportRepository.findAll();
        return toReportDtoList(reports);
    }

    public List<ReportDto> getReportsByStatus(String status) {
        List<Report> reports = reportRepository.findByStatus(status.toUpperCase());
        return toReportDtoList(reports);
    }

    public ReportDto resolveReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        report.setStatus("RESOLVED");
        return toReportDto(reportRepository.save(report));
    }

    public ReportDto rejectReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        report.setStatus("REJECTED");
        return toReportDto(reportRepository.save(report));
    }

    private List<ReportDto> toReportDtoList(List<Report> reports) {
        List<ReportDto> dtos = new ArrayList<>();
        for (Report report : reports) {
            dtos.add(toReportDto(report));
        }
        return dtos;
    }

    private ReportDto toReportDto(Report report) {
        ReportDto dto = new ReportDto();
        dto.setId(report.getId());
        dto.setReportType(report.getReportType());
        dto.setReason(report.getReason());
        dto.setDescription(report.getDescription());
        dto.setStatus(report.getStatus());
        dto.setCreatedAt(report.getCreatedAt());

        if (report.getReportedByUser() != null) {
            dto.setReportedByUserId(report.getReportedByUser().getId());
            dto.setReportedByUserName(report.getReportedByUser().getName());
        }
        if (report.getReportedToJob() != null) {
            dto.setReportedToJobId(report.getReportedToJob().getId());
            dto.setReportedToJobTitle(report.getReportedToJob().getTitle());
        }
        if (report.getReportedToUser() != null) {
            dto.setReportedToUserId(report.getReportedToUser().getId());
            dto.setReportedToUserName(report.getReportedToUser().getName());
        }

        return dto;
    }

    public List<ReportDto>getMyreports(String email) {
        List<Report> byreportedByEmail = reportRepository.findByReportedByUser_Email(email);
        if (byreportedByEmail.isEmpty()) {
            throw new RuntimeException("Report not found");
        }
        List<ReportDto> dtos = new ArrayList<>();
        for (Report report : byreportedByEmail) {
          dtos.add(toReportDto(report));

        }
        return dtos;

    }
}
