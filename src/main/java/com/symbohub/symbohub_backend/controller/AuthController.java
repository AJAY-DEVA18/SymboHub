package com.symbohub.controller;

import com.symbohub.symbohub_backend.dto.*;
import com.symbohub.symbohub_backend.service.CollegeService;
import com.symbohub.symbohub_backend.service.DepartmentService;
import com.symbohub.symbohub_backend.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AuthController {

    private final CollegeService collegeService;
    private final DepartmentService departmentService;
    private final AdminService adminService;

    @PostMapping("/college/login")
    public ResponseEntity<ApiResponse<LoginResponse>> loginCollege(@Valid @RequestBody CollegeLoginDTO loginDTO) {
        try {
            log.info("College login attempt via AuthController for: {}", loginDTO.getEmail());
            LoginResponse response = collegeService.loginCollege(loginDTO);
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        } catch (Exception e) {
            log.error("College login failed for: {}", loginDTO.getEmail(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/department/login")
    public ResponseEntity<ApiResponse<LoginResponse>> loginDepartment(@Valid @RequestBody DepartmentLoginDTO loginDTO) {
        try {
            log.info("Department login attempt via AuthController for: {}", loginDTO.getEmail());
            LoginResponse response = departmentService.loginDepartment(loginDTO);
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        } catch (Exception e) {
            log.error("Department login failed for: {}", loginDTO.getEmail(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/admin/login")
    public ResponseEntity<ApiResponse<LoginResponse>> loginAdmin(@Valid @RequestBody AdminLoginRequest loginDTO) {
        try {
            log.info("Admin login attempt via AuthController for: {}", loginDTO.getUsername());
            LoginResponse response = adminService.login(loginDTO);
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        } catch (Exception e) {
            log.error("Admin login failed for: {}", loginDTO.getUsername(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}