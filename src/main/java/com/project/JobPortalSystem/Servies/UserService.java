package com.project.JobPortalSystem.Servies;

import com.project.JobPortalSystem.DTO.JobDto;
import com.project.JobPortalSystem.Entity.UserRole;
import com.project.JobPortalSystem.Entity.UserStatus;
import com.project.JobPortalSystem.Entity.users;
import com.project.JobPortalSystem.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UserService {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private RedisService redisService;

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public String validateAndSendOtp(users user) throws Exception{
        if (user.getRole()==null||user.getEmail()==null||user.getName()==null||user.getPassword()==null){
            throw new Exception("Please fill all the details");
        }

        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!user.getEmail().matches(emailRegex)) {
            throw new Exception("Invalid email format");
        }

        if (user.getRole() != UserRole.JOB_SEEKER && user.getRole() != UserRole.RECRUITER) {
            throw new Exception("Only JOB_SEEKER and RECRUITER can sign up");
        }

        if (existsByEmail(user.getEmail())) {
            throw new Exception("You already have an account");
        }

        if (!SendOtpEmail(user.getEmail(),user.getName())) {
            throw new Exception("Something is wrong");
        }

       try {
           redisService.set(user.getEmail(), user, 600L);
       }catch (Exception e){}

        return "OTP sent successfully";
    }

    public String createUser(String email) throws Exception{
        try {
            users user = redisService.get(email, users.class);
            user.setStatus(UserStatus.ACTIVE);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            users saveUser = userRepository.save(user);
            emailService.sendEmail(saveUser.getEmail(),
                    "Welcome to Job Portal - Registration Successful",
                    """
                    Hello %s,
    
                    Thank you for registering with Job Portal!
    
                    Your account has been created successfully.
    
                    Best regards,
                    Job Portal Team""".formatted(user.getName()));
            return "Your account created successfully.";
        }catch (Exception e){
            throw new Exception("Something is wrong");
        }
    }

    public boolean SendOtpEmail(String email,String name) {
        Random random = new Random();

        String otp = String.format("%06d", random.nextInt(1000000));

        emailService.sendEmail(email,
                "Welcome to Job Portal - Verify Your Email",
                """
                Hello %s,
                
                Your verification code is
                
                %s
                
                This OTP is valid for 5 minutes.
             
                Do not share it with anyone.
                
                Best regards,
                Job Portal Team""".formatted(name,otp));

        try {
            redisService.set(otp, email,300L);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean verifyOtp(String email,String otp) {
        try {
            String storedOtp = redisService.get(otp, String.class);
            if (storedOtp != null) {
                return true;
            } else {
              return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public String resetOtp(String email) throws Exception {
            users user = redisService.get(email, users.class);
            if(user==null){
                throw new Exception("User not found");
            }
            if(SendOtpEmail(email, user.getName())) {
                return "OTP sent successfully";
            }
            else {
                return "Something is wrong";
            }

    }
}