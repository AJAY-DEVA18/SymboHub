package com.symbohub.symbohub_backend.controller;

import com.symbohub.symbohub_backend.dto.*;
import com.symbohub.symbohub_backend.service.AdminService;
import com.symbohub.symbohub_backend.service.CollegeService;
import com.symbohub.symbohub_backend.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;
    private final CollegeService collegeService;
    private final DepartmentService departmentService;

    // Admin login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> adminLogin(
            @Valid @RequestBody AdminLoginRequest loginDTO) {
        try {
            log.info("Admin login attempt for: {}", loginDTO.getUsername());

            LoginResponse response = adminService.login(loginDTO);
            return ResponseEntity.ok(ApiResponse.success("Admin login successful", response));
        } catch (Exception e) {
            log.error("Admin login failed for: {}", loginDTO.getUsername(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid admin credentials"));
        }
    }

    // Get admin dashboard
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardDTO>> getAdminDashboard() {
        try {
            AdminDashboardDTO dashboard = adminService.getDashboard();
            return ResponseEntity.ok(ApiResponse.success("Admin dashboard retrieved", dashboard));
        } catch (Exception e) {
            log.error("Failed to retrieve admin dashboard", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Unauthorized"));
        }
    }

    // Get pending colleges
    @GetMapping("/colleges/pending")
    public ResponseEntity<ApiResponse<List<CollegeDTO>>> getPendingColleges() {
        try {
            List<CollegeDTO> colleges = adminService.getPendingColleges();
            return ResponseEntity.ok(ApiResponse.success("Pending colleges retrieved", colleges));
        } catch (Exception e) {
            log.error("Failed to retrieve pending colleges", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Unauthorized"));
        }
    }

    // Get approved colleges
    @GetMapping("/colleges/approved")
    public ResponseEntity<ApiResponse<List<CollegeDTO>>> getApprovedColleges() {
        try {
            List<CollegeDTO> colleges = adminService.getApprovedColleges();
            return ResponseEntity.ok(ApiResponse.success("Approved colleges retrieved", colleges));
        } catch (Exception e) {
            log.error("Failed to retrieve approved colleges", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Unauthorized"));
        }
    }

    // Approve college
    @PutMapping("/colleges/{collegeId}/approve")
    public ResponseEntity<ApiResponse<CollegeDTO>> approveCollege(@PathVariable Long collegeId) {
        try {
            CollegeDTO approvedCollege = adminService.approveCollege(collegeId);
            return ResponseEntity.ok(ApiResponse.success("College approved successfully", approvedCollege));
        } catch (Exception e) {
            log.error("Failed to approve college: {}", collegeId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Reject college
    @PutMapping("/colleges/{collegeId}/reject")
    public ResponseEntity<ApiResponse<CollegeDTO>> rejectCollege(
            @PathVariable Long collegeId,
            @RequestParam(required = false) String reason) {
        try {
            CollegeDTO rejectedCollege = adminService.rejectCollege(collegeId, reason);
            return ResponseEntity.ok(ApiResponse.success("College rejected successfully", rejectedCollege));
        } catch (Exception e) {
            log.error("Failed to reject college: {}", collegeId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Delete college
    @DeleteMapping("/colleges/{collegeId}")
    public ResponseEntity<ApiResponse<Void>> deleteCollege(@PathVariable Long collegeId) {
        try {
            adminService.deleteCollege(collegeId);
            return ResponseEntity.ok(ApiResponse.success("College deleted successfully"));
        } catch (Exception e) {
            log.error("Failed to delete college: {}", collegeId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Delete department
    @DeleteMapping("/departments/{departmentId}")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(@PathVariable Long departmentId) {
        try {
            adminService.deleteDepartment(departmentId);
            return ResponseEntity.ok(ApiResponse.success("Department deleted successfully"));
        } catch (Exception e) {
            log.error("Failed to delete department: {}", departmentId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Get all departments
    @GetMapping("/departments")
    public ResponseEntity<ApiResponse<List<DepartmentDTO>>> getAllDepartments() {
        try {
            // This would need a method to get all departments across all colleges
            return ResponseEntity.ok(ApiResponse.success("Feature not implemented yet"));
        } catch (Exception e) {
            log.error("Failed to retrieve departments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve departments"));
        }
    }
}