package com.project.JobPortalSystem.Controller;

import com.project.JobPortalSystem.DTO.JobDto;
import com.project.JobPortalSystem.Entity.jobs;
import com.project.JobPortalSystem.Servies.JobsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("job")
public class JobController {

    @Autowired
    private JobsService jobsService;


    @PostMapping("create")
    public ResponseEntity<?> createJob(@RequestBody jobs job) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            String recruterEmail = authentication.getName();
            jobsService.createJob(job, recruterEmail);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception exception) {
            return new ResponseEntity<>(exception.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping("update")
    public ResponseEntity<?> updateJob(@RequestBody jobs job) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            String recruterEmail = authentication.getName();
            jobsService.updateJob(job, recruterEmail);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("delete")
    public ResponseEntity<?> deleteJob(@RequestParam Long jobId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            String recruterEmail = authentication.getName();
            return jobsService.ArchiveJob(jobId, recruterEmail);
        } catch (Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);

        }
    }

    //search job as keyword
    @GetMapping("search")
    public ResponseEntity<?> searchJobs(@RequestParam String keyword) {

        List<com.project.JobPortalSystem.DTO.JobDto> jobs = jobsService.searchJobs(keyword);

        if (jobs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No jobs found");
        }

        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/recruiter/search")
    public ResponseEntity<?> searchJobsRecruiter() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        List<com.project.JobPortalSystem.DTO.JobDto> jobs = jobsService.searchJobsRecruiter(userEmail);

        if (jobs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No jobs found");
        }

        return ResponseEntity.ok(jobs);
    }
    @GetMapping("/recruiter/searchs")
    public ResponseEntity<?> searchJobsRecruiter(@RequestParam String keyword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<com.project.JobPortalSystem.DTO.JobDto> jobs= jobsService.searchJobsRecruiter(authentication.getName(),keyword);

        if (jobs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No jobs found");
        }

        return ResponseEntity.ok(jobs);
    }

}
