package com.symbohub.symbohub_backend.controller;



import com.symbohub.symbohub_backend.dto.*;
import com.symbohub.symbohub_backend.service.BrochureService;
import com.symbohub.symbohub_backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/brochures")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class BrochureController {

    private final BrochureService brochureService;
    private final EmailService emailService;

    // Upload brochure
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<BrochureUploadResult>> uploadBrochure(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "departmentIds", required = false) List<Long> departmentIds,
            @RequestParam(value = "isPublic", defaultValue = "true") boolean isPublic,
            @RequestParam("departmentId") Long departmentId) {

        try {
            log.info("Uploading brochure: {} by department: {}", title, departmentId);

            CreateBrochureDTO brochureDTO = new CreateBrochureDTO();
            brochureDTO.setTitle(title);
            brochureDTO.setDescription(description);
            brochureDTO.setDepartmentIds(departmentIds);
            brochureDTO.setPublic(isPublic);

            BrochureUploadResult result = brochureService.uploadBrochure(brochureDTO, file, departmentId);

            return ResponseEntity.ok(ApiResponse.success(
                    "Brochure uploaded successfully",
                    result
            ));
        } catch (Exception e) {
            log.error("Failed to upload brochure: {}", title, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Get department brochures
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<ApiResponse<List<BrochureDTO>>> getDepartmentBrochures(
            @PathVariable Long departmentId) {
        try {
            List<BrochureDTO> brochures = brochureService.getDepartmentBrochures(departmentId);
            return ResponseEntity.ok(ApiResponse.success("Brochures retrieved", brochures));
        } catch (Exception e) {
            log.error("Failed to retrieve brochures for department: {}", departmentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve brochures"));
        }
    }

    // Get public brochures by college
    @GetMapping("/college/{collegeId}/public")
    public ResponseEntity<ApiResponse<List<BrochureDTO>>> getPublicBrochuresByCollege(
            @PathVariable Long collegeId) {
        try {
            List<BrochureDTO> brochures = brochureService.getPublicBrochuresByCollege(collegeId);
            return ResponseEntity.ok(ApiResponse.success("Public brochures retrieved", brochures));
        } catch (Exception e) {
            log.error("Failed to retrieve public brochures for college: {}", collegeId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve brochures"));
        }
    }

    // Get public brochures by department
    @GetMapping("/department/{departmentId}/public")
    public ResponseEntity<ApiResponse<List<BrochureDTO>>> getPublicBrochuresByDepartment(
            @PathVariable Long departmentId) {
        try {
            List<BrochureDTO> brochures = brochureService.getPublicBrochuresByDepartment(departmentId);
            return ResponseEntity.ok(ApiResponse.success("Public brochures retrieved", brochures));
        } catch (Exception e) {
            log.error("Failed to retrieve public brochures for department: {}", departmentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve brochures"));
        }
    }

    // Get brochure by ID
    @GetMapping("/{brochureId}")
    public ResponseEntity<ApiResponse<BrochureDTO>> getBrochureById(@PathVariable Long brochureId) {
        try {
            BrochureDTO brochure = brochureService.getBrochureById(brochureId);
            return ResponseEntity.ok(ApiResponse.success("Brochure retrieved", brochure));
        } catch (Exception e) {
            log.error("Failed to retrieve brochure: {}", brochureId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Brochure not found"));
        }
    }

    // Delete brochure
    @DeleteMapping("/{brochureId}")
    public ResponseEntity<ApiResponse<Void>> deleteBrochure(@PathVariable Long brochureId) {
        try {
            brochureService.deleteBrochure(brochureId);
            return ResponseEntity.ok(ApiResponse.success("Brochure deleted successfully"));
        } catch (Exception e) {
            log.error("Failed to delete brochure: {}", brochureId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}