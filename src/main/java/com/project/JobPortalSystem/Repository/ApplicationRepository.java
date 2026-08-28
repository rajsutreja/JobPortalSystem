package com.project.JobPortalSystem.Repository;

import com.project.JobPortalSystem.Entity.application;
import com.project.JobPortalSystem.Entity.jobs;
import com.project.JobPortalSystem.Entity.users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<application, Long> {
    Optional<application> findByUserAndJob(users user, jobs job);
    List<application> findByUserEmailAndJob_id(String userEmail,long jobid);
    boolean existsByJobId(Long jobId);

    @Query("SELECT a FROM application a WHERE a.job.recruiter.id = :recruiterId")
    List<application> findByRecruiterId(@Param("recruiterId") Long recruiterId);

    @Query("SELECT a FROM application a WHERE a.job.id = :jobId AND a.job.recruiter.id = :recruiterId")
    List<application> findByJobIdAndRecruiterId(@Param("jobId") Long jobId, @Param("recruiterId") Long recruiterId);

    application findByuser_id(long user_id);

    List<application> findByjob_id(long user_id);

}
