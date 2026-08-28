package com.project.JobPortalSystem.Servies;

import com.project.JobPortalSystem.DTO.ApplicationDTO;
import com.project.JobPortalSystem.DTO.DashboardDto;
import com.project.JobPortalSystem.DTO.JobDto;
import com.project.JobPortalSystem.DTO.UserDto;
import com.project.JobPortalSystem.Entity.UserRole;
import com.project.JobPortalSystem.Entity.UserStatus;
import com.project.JobPortalSystem.Entity.application;
import com.project.JobPortalSystem.Entity.jobs;
import com.project.JobPortalSystem.Entity.users;
import com.project.JobPortalSystem.Repository.ApplicationRepository;
import com.project.JobPortalSystem.Repository.JobsRepository;
import com.project.JobPortalSystem.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobsRepository jobsRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    public List<UserDto>getUsers() {
        List<users> list = userRepository.findAll();
        List<UserDto> dtos = new ArrayList<>();
        for(users users:list){
            dtos.add(toUserDto(users));
        }
        return dtos;
    }

    public List<UserDto> getUsersByName(String name) {
        List<users> byname = userRepository.findByname(name);
        List<UserDto> dtos = new ArrayList<>();
        for(users users:byname){
            dtos.add(toUserDto(users));
        }
        return dtos;
    }
    public List<UserDto> getUsersByEmail(String email) {
       Optional<users> byname = userRepository.findByEmail(email);
       List<users>user=byname.stream().toList();
        List<UserDto> dtos = new ArrayList<>();
        for(users users:user){
            dtos.add(toUserDto(users));
        }
        return dtos;
    }
//
//    public void deleteUserById(long id){
//        userRepository.deleteById(id);
//    }

    public UserDto blockUser(long id) {
        users user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(UserStatus.BLOCKED);
        return toUserDto(userRepository.save(user));
    }

    public UserDto activateUser(long id) {
        users user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(UserStatus.ACTIVE);
        return toUserDto(userRepository.save(user));
    }

    private UserDto toUserDto(users user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setRoles(user.getRole() == null ? null : user.getRole().name());
        userDto.setStatus(user.getStatus() == null ? UserStatus.ACTIVE.name() : user.getStatus().name());
        return userDto;
    }


    private JobDto jobDto(jobs job) {
        JobDto jobDto = new JobDto();
        jobDto.setId(job.getId());
        jobDto.setCompany(job.getCompany());
        jobDto.setTitle(job.getTitle());
        jobDto.setDescription(job.getDescription());
        jobDto.setStatus(job.getStatus());
        jobDto.setLocation(job.getLocation());
        jobDto.setSalary(job.getSalary());
        jobDto.setRecruiterName(job.getRecruiter().getName());
        return jobDto;
    }
    private ApplicationDTO applicationDTO(application application) {
        ApplicationDTO applicationDTO = new ApplicationDTO();
        applicationDTO.setApplicationId(application.getId());
        applicationDTO.setUserId(application.getUser().getId());
        applicationDTO.setStatus(application.getStatus());
        applicationDTO.setUserName(application.getUser().getName());
        applicationDTO.setUserEmail(application.getUser().getEmail());
        applicationDTO.setResumeUploaded(true);
        return applicationDTO;
    }

    public List<JobDto> GetAllJobs() {
        List<jobs> all = jobsRepository.findAll();
        List<JobDto> dtos = new ArrayList<>();
        for (jobs job:all) {
           dtos.add(jobDto(job));

        }
        return dtos;
    }

    public List<JobDto>GetJobByRecruiter(long recruiterId) {
        List<jobs> byrecruiterId = jobsRepository.findByrecruiterId(recruiterId);
        List<JobDto> dtos = new ArrayList<>();
        for (jobs job:byrecruiterId) {
            dtos.add(jobDto(job));
        }
        return dtos;
    }

    public void JobArchive(long jobid){
        jobs jobs = jobsRepository.findById(jobid).orElseThrow(() -> new RuntimeException("Job not found"));
        if(jobs.getStatus().equalsIgnoreCase("ARCHIVED")){
            throw new RuntimeException("Job Already Archived");
        }
        jobs.setStatus("ARCHIVED");
        jobsRepository.save(jobs);
    }

    public void JobActive(long jobid){
        jobs jobs = jobsRepository.findById(jobid).orElseThrow(() -> new RuntimeException("Job not found"));
        if(jobs.getStatus().equalsIgnoreCase("OPEN")){
            throw new RuntimeException("Job Already OPEN");
        }
        jobs.setStatus("OPEN");
        jobsRepository.save(jobs);
    }


    public List<ApplicationDTO>getApplicationsByJobId(long jobid) {
        jobsRepository.findById(jobid).orElseThrow(() -> new RuntimeException("Job not found"));
        List<application> byjobId = applicationRepository.findByjob_id(jobid);
        List<ApplicationDTO> dtos = new ArrayList<>();
        for (application application:byjobId) {
            dtos.add(applicationDTO(application));
        }
        if (dtos.isEmpty()) {
            throw new RuntimeException("Application not found for this Job");
        }
        return dtos;
    }

    public void deleteApplication(long ApplicationId) {
        applicationRepository.findById(ApplicationId).orElseThrow(() -> new RuntimeException("Application not found"));
        applicationRepository.deleteById(ApplicationId);
    }

    public DashboardDto getDashboard() {
        return new DashboardDto(
                userRepository.count(),
                userRepository.countByRole(UserRole.RECRUITER),
                userRepository.countByRole(UserRole.JOB_SEEKER),
                jobsRepository.count(),
                applicationRepository.count()
        );
    }

}
