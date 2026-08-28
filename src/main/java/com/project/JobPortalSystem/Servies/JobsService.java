package com.project.JobPortalSystem.Servies;

import com.project.JobPortalSystem.DTO.JobDto;
import com.project.JobPortalSystem.Entity.jobs;
import com.project.JobPortalSystem.Entity.UserRole;
import com.project.JobPortalSystem.Entity.users;
import com.project.JobPortalSystem.Repository.JobsRepository;
import com.project.JobPortalSystem.Repository.UserRepository;
import com.project.JobPortalSystem.Repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class JobsService {
    @Autowired
    private JobsRepository jobsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;
    
    @Autowired
    private RedisService redisService;

    public void createJob(jobs job, String recruterEmail) {
        users recruiter = userRepository.findByEmail(recruterEmail)
                .orElseThrow(() -> new IllegalArgumentException("Recruiter not found"));

        if (recruiter.getRole() != UserRole.RECRUITER) {
            throw new IllegalArgumentException("User is not a recruiter");
        }

        boolean exists = jobsRepository.existsByTitleIgnoreCaseAndCompanyIgnoreCaseAndRecruiterId(
                job.getTitle(), job.getCompany(), recruiter.getId());
        if (exists) {
            throw new IllegalArgumentException("Duplicate job: same title and company already posted by this recruiter");
        }

        // set recruiter and initial status
        job.setRecruiter(recruiter);
        job.setStatus("OPEN");
        jobsRepository.save(job);
    }

    public void updateJob(jobs job, String recruterEmail) {
        if (job.getId() == null) {
            throw new IllegalArgumentException("Job id is required");
        }

        jobs existingJob = jobsRepository.findById(job.getId())
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        users recruiter = userRepository.findByEmail(recruterEmail)
                .orElseThrow(() -> new IllegalArgumentException("Recruiter not found"));

        if (recruiter.getRole() != UserRole.RECRUITER) {
            throw new IllegalArgumentException("User is not a recruiter");
        }
        if (!existingJob.getRecruiter().getEmail().equals(recruiter.getEmail())) {
            throw new IllegalArgumentException("Unauthorized to update this job");
        }
        if ("ARCHIVED".equalsIgnoreCase(existingJob.getStatus())) {
            throw new IllegalArgumentException("Cannot update an archived job");
        }

        // Only overwrite fields that are provided (non-null). Preserve existing values otherwise.
        if (job.getTitle() != null && !job.getTitle().isBlank()) {
            existingJob.setTitle(job.getTitle());
        }
        if (job.getCompany() != null && !job.getCompany().isBlank()) {
            existingJob.setCompany(job.getCompany());
        }
        if (job.getSalary() != null) {
            existingJob.setSalary(job.getSalary());
        }
        if (job.getLocation() != null && !job.getLocation().isBlank()) {
            existingJob.setLocation(job.getLocation());
        }
        if (job.getDescription() != null && !job.getDescription().isBlank()) {
            existingJob.setDescription(job.getDescription());
        }

        // recruiter stays as the authenticated recruiter (no change), but ensure it's set on the saved entity
        existingJob.setRecruiter(recruiter);

        jobsRepository.save(existingJob);

    }

    public ResponseEntity<?> ArchiveJob(Long jobid, String  recruterEmail) {

        if (jobid == null) {
            throw new IllegalArgumentException("Job id is required");
        }

        Optional<jobs> validJob = jobsRepository.findById(jobid);

        if (validJob.isEmpty()) {
            return new ResponseEntity<>("Job not found", HttpStatus.NOT_FOUND);
        }

        jobs existingJob = validJob.get();

        // Check recruiter ownership
        if (!existingJob.getRecruiter().getEmail().equals(recruterEmail)) {
            return new ResponseEntity<>("Unauthorized to delete this job",
                    HttpStatus.FORBIDDEN);
        }

        // Archive job instead of deleting to preserve applications and history
        existingJob.setStatus("ARCHIVED");
        jobsRepository.save(existingJob);

        return new ResponseEntity<>("Job archived successfully", HttpStatus.OK);
    }

    public JobDto tojobDto(jobs j) {
        JobDto d = new JobDto();
        d.setId(j.getId());
        d.setTitle(j.getTitle());
        d.setCompany(j.getCompany());
        d.setLocation(j.getLocation());
        d.setSalary(j.getSalary());
        d.setDescription(j.getDescription());
        d.setStatus(j.getStatus());

        if (j.getRecruiter() != null) {
            d.setRecruiterName(j.getRecruiter().getName());
        }

        return d;
    }
    public List<JobDto> searchJobs(String keyword) {
        String cacheKey = "jobs:" + (keyword == null ? "all" : keyword.trim().toLowerCase());

        // Try to read cached results as an array, then convert to List
        try {
            JobDto[] cached = redisService.get(cacheKey, JobDto[].class);
            if (cached != null) {
                return java.util.Arrays.asList(cached);
            }
        } catch (Exception ignored) {
            // RedisService already logs errors; continue to fetch from DB on failure
        }

        List<jobs> results = jobsRepository.findByTitleContainingIgnoreCaseAndStatus(keyword, "OPEN");
        List<JobDto> dtoList = new java.util.ArrayList<JobDto>();
        for (jobs j : results) {
            dtoList.add( tojobDto(j));
        }

        // Cache the result for 5 minutes (300 seconds)
        try {
            redisService.set(cacheKey, dtoList, 300L);
        } catch (Exception ignored) {
            // ignore cache set failures
        }

        return dtoList;
    }

    public List<JobDto> searchJobsRecruiter(String recruiterEmail) {
        users recruiter = userRepository.findByEmail(recruiterEmail)
                .orElseThrow(() -> new IllegalArgumentException("Recruiter not found"));

        if (recruiter.getRole() != UserRole.RECRUITER) {
            throw new IllegalArgumentException("User is not a recruiter");
        }

       List<jobs> results = jobsRepository.findByrecruiterId(recruiter.getId());
        List<JobDto> dtoList = new java.util.ArrayList<JobDto>();
        for (jobs j : results) {
            dtoList.add( tojobDto(j));
        }

        return dtoList;
    }

    public List<JobDto> searchJobsRecruiter(String recruiterEmail, String keyword) {
        users recruiter = userRepository.findByEmail(recruiterEmail)
                .orElseThrow(() -> new IllegalArgumentException("Recruiter not found"));

        if (recruiter.getRole() != UserRole.RECRUITER) {
            throw new IllegalArgumentException("User is not a recruiter");
        }

        List<jobs> results = jobsRepository.findByTitleContainingIgnoreCaseAndRecruiterId(keyword, recruiter.getId());
        List<JobDto> dtoList = new java.util.ArrayList<JobDto>();
        for (jobs j : results) {
            dtoList.add( tojobDto(j));
        }
        return dtoList;
    }
}
