package com.project.JobPortalSystem.Repository;

import com.project.JobPortalSystem.Entity.UserRole;
import com.project.JobPortalSystem.Entity.users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<users, Long> {
    List<users> findByRole(UserRole role);
    long countByRole(UserRole role);
    Optional<users> findByEmail(String email);
    List<users> findByname(String username);
}
