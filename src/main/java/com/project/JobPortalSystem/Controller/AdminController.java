package com.project.JobPortalSystem.Controller;

import com.project.JobPortalSystem.DTO.UserDto;
import com.project.JobPortalSystem.Servies.AdminService;
import com.project.JobPortalSystem.Servies.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private ReportService reportService;

    //users api
    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        List<UserDto> users = adminService.getUsers();
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/users/name")
    public ResponseEntity<?> getUsersbyname(@RequestParam String name) {
        List<UserDto> users = adminService.getUsersByName(name);
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    @GetMapping("/users/email")
    public ResponseEntity<?> getUsersbyEmail(@RequestParam String email) {
        List<UserDto> users = adminService.getUsersByEmail(email);
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PatchMapping("/users/{id}/block")
    public ResponseEntity<?> blockUser(@PathVariable long id) {
        try {
            return new ResponseEntity<>(adminService.blockUser(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/users/{id}/active")
    public ResponseEntity<?> activateUser(@PathVariable long id) {
        try {
            return new ResponseEntity<>(adminService.activateUser(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

//    @DeleteMapping("/user/{id}")
//    public ResponseEntity<?> deleteUser(@PathVariable long id) {
//        try {
//            adminService.deleteUserById(id);
//            return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
//        }catch (Exception e){
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
//        }
//    }

    //job api
    @GetMapping("jobs")
    public ResponseEntity<?> getAllJobs() {
        try {
            return new ResponseEntity<>(adminService.GetAllJobs(), HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("jobs/recruiter/{recruiterid}")
    public ResponseEntity<?> getJobByRecruiter(@PathVariable long recruiterid) {
        try {
            return new ResponseEntity<>(adminService.GetJobByRecruiter(recruiterid), HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("jobs/{jobid}/archive")
    public ResponseEntity<?> ArchiveJob(@PathVariable long jobid) {
        try {
            adminService.JobArchive(jobid);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    @PatchMapping("jobs/{jobid}/open")
    public ResponseEntity<?> ActiveJob(@PathVariable long jobid) {
        try {
            adminService.JobActive(jobid);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }


    //Application
    @GetMapping("application/jobid/{jobid}")
    public  ResponseEntity<?>getApplicationByJobid(@PathVariable long jobid) {
        try {
            return new ResponseEntity<>(adminService.getApplicationsByJobId(jobid), HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("application/id/{applicationId}")
    public ResponseEntity<?> deleteApplication(@PathVariable long applicationId) {
        try {
            adminService.deleteApplication(applicationId);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("dashboard")
    public ResponseEntity<?> getDashboard() {
        try {
            return new ResponseEntity<>(adminService.getDashboard(), HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    //reports
    @GetMapping("reports")
    public ResponseEntity<?> getReports() {
        try {
            return new ResponseEntity<>(reportService.getAllReports(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("reports/status")
    public ResponseEntity<?> getReportsByStatus(@RequestParam String status) {
        try {
            return new ResponseEntity<>(reportService.getReportsByStatus(status), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("reports/{reportId}/resolve")
    public ResponseEntity<?> resolveReport(@PathVariable long reportId) {
        try {
            return new ResponseEntity<>(reportService.resolveReport(reportId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("reports/{reportId}/reject")
    public ResponseEntity<?> rejectReport(@PathVariable long reportId) {
        try {
            return new ResponseEntity<>(reportService.rejectReport(reportId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
