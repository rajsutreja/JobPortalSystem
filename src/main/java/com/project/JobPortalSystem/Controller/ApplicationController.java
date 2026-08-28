package com.project.JobPortalSystem.Controller;

import com.project.JobPortalSystem.DTO.ApplicationDTO;
import com.project.JobPortalSystem.Entity.application;
import com.project.JobPortalSystem.Servies.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("application")
public class ApplicationController {
    @Autowired
    private ApplicationService applicationService;

    @PostMapping("jobseeker/apply/{jobid}")
    public ResponseEntity<?> apply(@PathVariable Long jobid) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        try {
            application app = applicationService.applyToJob(jobid, userEmail);
            return ResponseEntity.status(HttpStatus.CREATED).body(app);
        } catch (IllegalArgumentException e) {
            String msg = e.getMessage();
            if (msg != null && msg.toLowerCase().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
            } else if (msg != null && msg.toLowerCase().contains("only job seekers")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(msg);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @GetMapping("user/{jobid}")
    public ResponseEntity<?> getUserApplication(@PathVariable long jobid) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        List<ApplicationDTO> applicationsdto = applicationService.getjobseekerApplications(userEmail, jobid);
        if (applicationsdto == null) {
            return ResponseEntity.notFound().build();
        } else{
            return new ResponseEntity<>(applicationService.getjobseekerApplications(authentication.getName(),jobid), HttpStatus.OK);
        }

    }


    @GetMapping("recruiter/{jobid}")
    public ResponseEntity<?> getJobApplicationByjobid(@PathVariable long jobid) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        try {
            var response = applicationService.getJobidApplicationsByRecruiterId(userEmail, jobid);
            if (response.getCount() == 0) {
                return new ResponseEntity<>("No applications found for this job", HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("recruiter/{applicationid}/change/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long applicationid, @RequestParam String status) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        try {
            String updatedStatus = applicationService.UpdateApplicationStatus(applicationid, status,userEmail);
            return new ResponseEntity<>(updatedStatus, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            String msg = e.getMessage();
            if (msg != null && msg.toLowerCase().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
            } else if (msg != null && msg.toLowerCase().contains("not authorized")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(msg);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
