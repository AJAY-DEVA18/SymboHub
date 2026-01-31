/*
package com.symbohub.symbohub_backend.controller;

import com.symbohub.symbohub_backend.dto.*;
import com.symbohub.symbohub_backend.service.CollegeService;
import com.symbohub.symbohub_backend.service.DepartmentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/college")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@PreAuthorize("hasRole('COLLEGE')")
public class CollegeControllers {

    private final CollegeService collegeService;
    private final DepartmentService departmentService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getCollegeProfile(HttpServletRequest request) {
        Long collegeId = (Long) request.getAttribute("collegeId");
        CollegeDTO college = collegeService.getCollegeById(collegeId);
        return ResponseEntity.ok(ApiResponse.success("College profile retrieved", college));
    }

    @GetMapping("/departments")
    public ResponseEntity<ApiResponse> getMyDepartments(HttpServletRequest request) {
        Long collegeId = (Long) request.getAttribute("collegeId");
        List<DepartmentDTO> departments = collegeService.getCollegeDepartments(collegeId);
        return ResponseEntity.ok(ApiResponse.success("Departments retrieved", departments));
    }

    @PostMapping("/departments")
    public ResponseEntity<ApiResponse> createDepartment(
            HttpServletRequest request,
            @Valid @RequestBody CreateDepartmentDTO departmentDTO) {

        Long collegeId = (Long) request.getAttribute("collegeId");
        departmentDTO.setCollegeId(collegeId);
        var department = departmentService.createDepartment(departmentDTO);
        return ResponseEntity.ok(ApiResponse.success("Department created successfully", department.getId()));
    }

    @GetMapping("/departments/{departmentId}")
    public ResponseEntity<ApiResponse> getDepartment(
            @PathVariable Long departmentId,
            HttpServletRequest request) {

        Long collegeId = (Long) request.getAttribute("collegeId");
        DepartmentDTO department = departmentService.getDepartmentById(departmentId);

        // Verify department belongs to this college
        if (!department.getCollegeId().equals(collegeId)) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error("You are not authorized to access this department"));
        }

        return ResponseEntity.ok(ApiResponse.success("Department retrieved", department));
    }

    @DeleteMapping("/departments/{departmentId}")
    public ResponseEntity<ApiResponse> deleteDepartment(
            @PathVariable Long departmentId,
            HttpServletRequest request) {

        Long collegeId = (Long) request.getAttribute("collegeId");
        DepartmentDTO department = departmentService.getDepartmentById(departmentId);

        // Verify department belongs to this college
        if (!department.getCollegeId().equals(collegeId)) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error("You are not authorized to delete this department"));
        }

        departmentService.deleteDepartment(departmentId);
        return ResponseEntity.ok(ApiResponse.success("Department deleted successfully"));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse> updateCollegeProfile(
            HttpServletRequest request,
            @Valid @RequestBody CollegeUpdateDTO updateDTO) {

        Long collegeId = (Long) request.getAttribute("collegeId");
        // Implement update logic
        return ResponseEntity.ok(ApiResponse.success("College profile updated successfully"));
    }
}*/
