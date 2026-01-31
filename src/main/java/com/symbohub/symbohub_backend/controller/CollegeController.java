package com.symbohub.symbohub_backend.controller;

import com.symbohub.symbohub_backend.dto.*;
import com.symbohub.symbohub_backend.service.CollegeService;
import com.symbohub.symbohub_backend.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/colleges")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CollegeController {

    private final CollegeService collegeService;
    private final EmailService emailService;

    // College Registration
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CollegeDTO>> registerCollege(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("address") String address,
            @RequestParam("staffIdFile") MultipartFile staffIdFile) {

        try {
            log.info("College registration request received for: {}", email);

            CollegeRegistrationDTO registrationDTO = new CollegeRegistrationDTO(name, email, password, address);
            CollegeDTO collegeDTO = collegeService.registerCollege(registrationDTO, staffIdFile);

            // Send confirmation email
            emailService.sendCollegeRegistrationConfirmation(collegeDTO);

            return ResponseEntity.ok(ApiResponse.success(
                    "College registered successfully. Pending admin approval.",
                    collegeDTO
            ));
        } catch (Exception e) {
            log.error("College registration failed for: {}", email, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // College Login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> loginCollege(@Valid @RequestBody CollegeLoginDTO loginDTO) {
        try {
            log.info("College login attempt for: {}", loginDTO.getEmail());

            LoginResponse response = collegeService.loginCollege(loginDTO);
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        } catch (Exception e) {
            log.error("College login failed for: {}", loginDTO.getEmail(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Get approved colleges (public endpoint)
    @GetMapping("/approved")
    public ResponseEntity<ApiResponse<List<CollegeDTO>>> getApprovedColleges() {
        try {
            List<CollegeDTO> colleges = collegeService.getApprovedColleges();
            return ResponseEntity.ok(ApiResponse.success("Approved colleges retrieved", colleges));
        } catch (Exception e) {
            log.error("Failed to retrieve approved colleges", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve colleges"));
        }
    }

    // Get college by ID
    @GetMapping("/{collegeId}")
    public ResponseEntity<ApiResponse<CollegeDTO>> getCollegeById(@PathVariable Long collegeId) {
        try {
            CollegeDTO college = collegeService.getCollegeById(collegeId);
            return ResponseEntity.ok(ApiResponse.success("College retrieved", college));
        } catch (Exception e) {
            log.error("Failed to retrieve college: {}", collegeId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Get college dashboard
    @GetMapping("/{collegeId}/dashboard")
    public ResponseEntity<ApiResponse<CollegeDashboardDTO>> getCollegeDashboard(@PathVariable Long collegeId) {
        try {
            CollegeDashboardDTO dashboard = collegeService.getCollegeDashboard(collegeId);
            return ResponseEntity.ok(ApiResponse.success("Dashboard data retrieved", dashboard));
        } catch (Exception e) {
            log.error("Failed to retrieve dashboard for college: {}", collegeId, e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Get pending colleges (admin only)
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<CollegeDTO>>> getPendingColleges() {
        try {
            List<CollegeDTO> colleges = collegeService.getPendingColleges();
            return ResponseEntity.ok(ApiResponse.success("Pending colleges retrieved", colleges));
        } catch (Exception e) {
            log.error("Failed to retrieve pending colleges", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve pending colleges"));
        }
    }
}