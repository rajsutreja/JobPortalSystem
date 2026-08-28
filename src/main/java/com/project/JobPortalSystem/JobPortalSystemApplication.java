package com.project.JobPortalSystem;

import com.project.JobPortalSystem.Entity.UserRole;
import com.project.JobPortalSystem.Entity.users;
import com.project.JobPortalSystem.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class JobPortalSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobPortalSystemApplication.class, args);
	}
//	@Bean
//	public CommandLineRunner createAdmin(UserRepository userRepository,
//                                         PasswordEncoder passwordEncoder) {
//
//		return new CommandLineRunner() {
//			@Override
//			public void run(String... args) throws Exception {
//
//				if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
//
//					users admin = new users();
//					admin.setName("ss");
//					admin.setEmail("s@gmail.com");
//					admin.setPassword(passwordEncoder.encode("s123"));
//					admin.setRole(UserRole.ADMIN);
//
//					userRepository.save(admin);
//				}
//			}
//		};
//	}
}
