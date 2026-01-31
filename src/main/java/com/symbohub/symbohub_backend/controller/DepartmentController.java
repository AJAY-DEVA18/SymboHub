package com.symbohub.symbohub_backend.controller;

import com.symbohub.symbohub_backend.dto.*;
import com.symbohub.symbohub_backend.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DepartmentController {

    private final DepartmentService departmentService;

    // Create new department
    @PostMapping
    public ResponseEntity<ApiResponse<DepartmentDTO>> createDepartment(
            @Valid @RequestBody CreateDepartmentDTO departmentDTO) {
        try {
            log.info("Creating department: {}", departmentDTO.getEmail());

            DepartmentDTO createdDepartment = departmentService.createDepartment(departmentDTO);
            return ResponseEntity.ok(ApiResponse.success(
                    "Department created successfully",
                    createdDepartment
            ));
        } catch (Exception e) {
            log.error("Failed to create department: {}", departmentDTO.getEmail(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Department login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> loginDepartment(
            @Valid @RequestBody DepartmentLoginDTO loginDTO) {
        try {
            log.info("Department login attempt for: {}", loginDTO.getEmail());

            LoginResponse response = departmentService.loginDepartment(loginDTO);
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        } catch (Exception e) {
            log.error("Department login failed for: {}", loginDTO.getEmail(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Get department dashboard
    @GetMapping("/{departmentId}/dashboard")
    public ResponseEntity<ApiResponse<DepartmentDashboardDTO>> getDepartmentDashboard(
            @PathVariable Long departmentId) {
        try {
            DepartmentDashboardDTO dashboard = departmentService.getDepartmentDashboard(departmentId);
            return ResponseEntity.ok(ApiResponse.success("Dashboard data retrieved", dashboard));
        } catch (Exception e) {
            log.error("Failed to retrieve dashboard for department: {}", departmentId, e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Get department by ID
    @GetMapping("/{departmentId}")
    public ResponseEntity<ApiResponse<DepartmentDTO>> getDepartmentById(@PathVariable Long departmentId) {
        try {
            DepartmentDTO department = departmentService.getDepartmentById(departmentId);
            return ResponseEntity.ok(ApiResponse.success("Department retrieved", department));
        } catch (Exception e) {
            log.error("Failed to retrieve department: {}", departmentId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Get college departments
    @GetMapping("/college/{collegeId}")
    public ResponseEntity<ApiResponse<List<DepartmentDTO>>> getCollegeDepartments(
            @PathVariable Long collegeId) {
        try {
            List<DepartmentDTO> departments = departmentService.getCollegeDepartments(collegeId);
            return ResponseEntity.ok(ApiResponse.success("Departments retrieved", departments));
        } catch (Exception e) {
            log.error("Failed to retrieve departments for college: {}", collegeId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve departments"));
        }
    }

    // Update department
    @PutMapping("/{departmentId}")
    public ResponseEntity<ApiResponse<Void>> updateDepartment(
            @PathVariable Long departmentId,
            @Valid @RequestBody CreateDepartmentDTO updateDTO) {
        try {
            departmentService.updateDepartment(departmentId, updateDTO);
            return ResponseEntity.ok(ApiResponse.success("Department updated successfully"));
        } catch (Exception e) {
            log.error("Failed to update department: {}", departmentId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Delete department
    @DeleteMapping("/{departmentId}")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(@PathVariable Long departmentId) {
        try {
            departmentService.deleteDepartment(departmentId);
            return ResponseEntity.ok(ApiResponse.success("Department deleted successfully"));
        } catch (Exception e) {
            log.error("Failed to delete department: {}", departmentId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}