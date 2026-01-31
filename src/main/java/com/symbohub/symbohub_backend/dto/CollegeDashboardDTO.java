package com.symbohub.symbohub_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollegeDashboardDTO {
    private CollegeDTO college;
    private DashboardStats stats;
    private java.util.List<DepartmentDTO> recentDepartments;
    private java.util.List<BrochureDTO> recentBrochures;
}