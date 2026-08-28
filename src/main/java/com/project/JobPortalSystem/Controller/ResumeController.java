package com.project.JobPortalSystem.Controller;

import com.project.JobPortalSystem.Entity.resume;
import com.project.JobPortalSystem.Servies.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("resume")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @PostMapping("upload/jobseeker")
    public ResponseEntity<?> uploadResume(@RequestParam("file") MultipartFile file) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        if (file == null || file.isEmpty()) return new ResponseEntity<>("No file uploaded", HttpStatus.BAD_REQUEST);
        try {
            resumeService.saveResume(file,userEmail);
            return ResponseEntity.ok("Resume uploaded");
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to store file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("delete/jobseeker")
    public ResponseEntity<?> deleteResume(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        try {
            resumeService.DeleteResumeJobseeker(userEmail);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("recruiter/application/{applicationid}/jobseeker")
    public ResponseEntity<?> viewJobseekerResume(@PathVariable Long applicationid) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String recruiterEmail = authentication.getName();

        try {
            resume jobseekerResume = resumeService.getJobseekerResumeForApplication(applicationid, recruiterEmail);
            Path filePath = Path.of(jobseekerResume.getFilePath());

            if (!Files.exists(filePath)) {
                return new ResponseEntity<>("Resume file not found", HttpStatus.NOT_FOUND);
            }

            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(filePath));
            MediaType mediaType = jobseekerResume.getFileType() == null
                    ? MediaType.APPLICATION_OCTET_STREAM
                    : MediaType.parseMediaType(jobseekerResume.getFileType());

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + jobseekerResume.getFileName() + "\"")
                    .contentLength(resource.contentLength())
                    .body(resource);
        } catch (RuntimeException e) {
            String msg = e.getMessage();
            if (msg != null && msg.toLowerCase().contains("not found")) {
                return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
            }
            if (msg != null && (msg.toLowerCase().contains("only recruiters") || msg.toLowerCase().contains("not authorized"))) {
                return new ResponseEntity<>(msg, HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to read resume file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @GetMapping("jobseeker")
    public ResponseEntity<?>getJobseekerResume() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        try {
            resume jobseekerResume = resumeService.getJobseekerResumeForUser(userEmail);
            Path filePath = Path.of(jobseekerResume.getFilePath());

            if (!Files.exists(filePath)) {
                return new ResponseEntity<>("Resume file not found", HttpStatus.NOT_FOUND);
            }

            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(filePath));
            MediaType mediaType = jobseekerResume.getFileType() == null
                    ? MediaType.APPLICATION_OCTET_STREAM
                    : MediaType.parseMediaType(jobseekerResume.getFileType());

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + jobseekerResume.getFileName() + "\"")
                    .contentLength(resource.contentLength())
                    .body(resource);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
