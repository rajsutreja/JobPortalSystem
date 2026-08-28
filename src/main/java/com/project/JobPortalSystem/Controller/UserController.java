package com.project.JobPortalSystem.Controller;

import com.project.JobPortalSystem.DTO.VerifyOtpRequestDTO;
import com.project.JobPortalSystem.Entity.UserRole;
import com.project.JobPortalSystem.Entity.users;
import com.project.JobPortalSystem.Servies.UserService;
import com.project.JobPortalSystem.Utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("signup")
    public ResponseEntity<?> CreateJobSeeker(@RequestBody users user){
        try {
            return new ResponseEntity<>(userService.validateAndSendOtp(user), HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("verify-otp")
    public ResponseEntity<?> VerifyOtp(@RequestBody VerifyOtpRequestDTO verifyOtpRequestDTO){
        try {
            if (userService.verifyOtp(verifyOtpRequestDTO.getEmail(), verifyOtpRequestDTO.getOtp()))
            {
               return new ResponseEntity<>(userService.createUser(verifyOtpRequestDTO.getEmail()),HttpStatus.OK);
            }
            else  {
                return new ResponseEntity<>("OTP NOT VALID",HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("resend-otp")
    public ResponseEntity<?> ResendOtp(@RequestBody VerifyOtpRequestDTO verifyOtpRequestDTO){
        try {
                return new ResponseEntity<>(userService.resetOtp(verifyOtpRequestDTO.getEmail()),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody users user) {
        if (user.getEmail() == null ||user.getPassword() == null) {
            return new ResponseEntity<>("Email and password are required", HttpStatus.BAD_REQUEST);
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(),user.getPassword())
            );
            String jwttoken = jwtUtil.generateToken(user.getEmail());
            return new ResponseEntity<>(jwttoken, HttpStatus.OK);
        } catch (AuthenticationException exception) {
            return new ResponseEntity<>("Incorrect email or password", HttpStatus.UNAUTHORIZED);
        }
    }

}
