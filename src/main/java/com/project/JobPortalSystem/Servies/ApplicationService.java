package com.project.JobPortalSystem.Servies;

import com.project.JobPortalSystem.Entity.application;
import com.project.JobPortalSystem.Entity.jobs;
import com.project.JobPortalSystem.Entity.UserRole;
import com.project.JobPortalSystem.Entity.users;
import com.project.JobPortalSystem.Repository.ApplicationRepository;
import com.project.JobPortalSystem.Repository.JobsRepository;
import com.project.JobPortalSystem.Repository.ResumeRepository;
import com.project.JobPortalSystem.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.project.JobPortalSystem.DTO.ApplicationDTO;
import com.project.JobPortalSystem.DTO.ApplicationsResponse;
import java.util.ArrayList;

import java.util.List;

@Service
public class ApplicationService {
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private JobsRepository jobsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ResumeRepository  resumeRepository;


    public application applyToJob(Long jobId, String userEmail) {
        jobs job = jobsRepository.findByIdAndStatus(jobId,"OPEN")
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        users user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getRole() != UserRole.JOB_SEEKER) {
            throw new IllegalArgumentException("Only job seekers can apply");
        }

        if (applicationRepository.findByUserAndJob(user, job).isPresent()) {
            throw new IllegalArgumentException("User has already applied to this job");
        }
        //check jobseeker uploaded resume
        if (resumeRepository.findByUser_Id(user.getId()).isEmpty()){
            throw new IllegalArgumentException("job seeker not upload resume");
        }

        application app = new application();
        app.setJob(job);
        app.setUser(user);
        app.setStatus("Applied");

        return applicationRepository.save(app);
    }

    public ApplicationDTO getDto(application a) {
        ApplicationDTO dto = new ApplicationDTO();
        dto.setApplicationId(a.getId());
        dto.setStatus(a.getStatus());
        if (a.getUser() != null) {
            dto.setUserId(a.getUser().getId());
            dto.setUserName(a.getUser().getName());
            dto.setUserEmail(a.getUser().getEmail());
            dto.setResumeUploaded(resumeRepository.findByUser_Id(a.getUser().getId()).isPresent());
        } else {
            dto.setResumeUploaded(false);
        }
        return dto;

    }
    public List<ApplicationDTO> getjobseekerApplications(String jobseekerEmail,long jobid) {
        List<application> byUserEmailAndJobId = applicationRepository.findByUserEmailAndJob_id(jobseekerEmail, jobid);
        List<ApplicationDTO> dtos = new ArrayList<>();

        for (application a : byUserEmailAndJobId) {
            dtos.add(getDto(a));
        }
        return dtos;
    }

    public ApplicationsResponse getJobidApplicationsByRecruiterId(String recruterEmail, Long jobid) {
        users user = userRepository.findByEmail(recruterEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getRole() != UserRole.RECRUITER) {
            throw new IllegalArgumentException("You are not authorized to view this job");
        }

        // Verify the job exists and is owned by the recruiter
        jobs job = jobsRepository.findById(jobid)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        if (!job.getRecruiter().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You are not authorized to view applications for this job");
        }

        List<application> applications = applicationRepository.findByJobIdAndRecruiterId(jobid, user.getId());

        List<ApplicationDTO> dtos = new ArrayList<>();
        for (application a : applications) {
            dtos.add(getDto(a));
        }

        ApplicationsResponse resp = new ApplicationsResponse();
        resp.setCount(dtos.size());
        resp.setApplications(dtos);
        return resp;
    }

    public String UpdateApplicationStatus(Long id, String Status, String recruterEmail) {
        application application = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        jobs job = application.getJob();
        if (job == null) {
            throw new IllegalArgumentException("Associated job not found");
        }

        users recruiter = job.getRecruiter();
        if (recruiter == null || recruiter.getId() == null || !recruiter.getEmail().equals(recruterEmail)) {
            throw new IllegalArgumentException("You are not authorized to update this application");
        }

        application.setStatus(Status);
        return applicationRepository.save(application).getStatus();
    }
}
