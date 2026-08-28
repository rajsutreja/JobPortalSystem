package com.project.JobPortalSystem.Servies;

import com.project.JobPortalSystem.Entity.application;
import com.project.JobPortalSystem.Entity.resume;
import com.project.JobPortalSystem.Entity.UserRole;
import com.project.JobPortalSystem.Entity.users;
import com.project.JobPortalSystem.Repository.ApplicationRepository;
import com.project.JobPortalSystem.Repository.ResumeRepository;
import com.project.JobPortalSystem.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class ResumeService {

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    private final Path rootLocation = Paths.get("uploads\\resumes");

    public resume saveResume(MultipartFile file,String userEmail) throws IOException {
        users user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != UserRole.JOB_SEEKER) {
            throw new RuntimeException("Only job seekers can upload resume");
        }

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("No file provided");
        }

        Files.createDirectories(rootLocation);

        String originalFilename = Path.of(file.getOriginalFilename()).getFileName().toString();
        String filename = System.currentTimeMillis() + "_" + originalFilename;
        Path destinationFile = rootLocation.resolve(filename).normalize().toAbsolutePath();

        if (!destinationFile.startsWith(rootLocation.toAbsolutePath())) {
            throw new RuntimeException("Cannot store file outside the configured directory.");
        }

        try (var inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        }

        resume existing = resumeRepository.findByUser_Id(user.getId()).orElse(null);
        if (existing != null) {
            if (existing.getFilePath() != null) {
                try {
                    Files.deleteIfExists(Paths.get(existing.getFilePath()));
                } catch (IOException ignored) { }
            }
            existing.setFileName(originalFilename);
            existing.setFilePath(destinationFile.toString());
            existing.setFileType(file.getContentType());
            return resumeRepository.save(existing);
        } else {
            resume r = new resume();
            r.setFileName(originalFilename);
            r.setFilePath(destinationFile.toString());
            r.setFileType(file.getContentType());
            r.setUser(user);
            return resumeRepository.save(r);
        }
    }

    public void DeleteResumeJobseeker(String userEmail) {
        try {
            users users = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
            if (users.getRole() != UserRole.JOB_SEEKER) {
                throw new RuntimeException("Only Job seekers can delete resume");
            }

            resume resume = resumeRepository.findByUser_Id(users.getId())
                    .orElseThrow(() -> new RuntimeException("You have not uploaded a resume"));


            application byuserId = applicationRepository.findByuser_id(users.getId());
            if (byuserId == null ||
                    (!byuserId.getStatus().equalsIgnoreCase("REJECTED") &&
                            !byuserId.getStatus().equalsIgnoreCase("SELECTED"))) {
                throw new RuntimeException("Your resume has been not deleted.Beacause your application not rejected/selected.");
            }
          resumeRepository.delete(resume);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }




    public resume getJobseekerResumeForApplication(Long applicationId, String recruiterEmail) {
        users recruiter = userRepository.findByEmail(recruiterEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (recruiter.getRole() != UserRole.RECRUITER) {
            throw new RuntimeException("Only recruiters can view job seeker resume");
        }

        application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (application.getJob() == null || application.getJob().getRecruiter() == null
                || !application.getJob().getRecruiter().getId().equals(recruiter.getId())) {
            throw new RuntimeException("You are not authorized to view this resume");
        }

        users jobseeker = application.getUser();
        if (jobseeker == null || jobseeker.getId() == null) {
            throw new RuntimeException("Job seeker not found for this application");
        }

        return resumeRepository.findByUser_Id(jobseeker.getId())
                .orElseThrow(() -> new RuntimeException("Job seeker resume not found"));
    }


    public resume getJobseekerResumeForUser(String userEmail) {
        users user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return resumeRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new RuntimeException("You have not uploaded a resume"));

    }
}
