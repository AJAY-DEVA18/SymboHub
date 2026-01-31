package com.symbohub.symbohub_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDTO {
    private DashboardStats stats;
    private java.util.List<CollegeDTO> recentPendingColleges;
    private java.util.List<CollegeDTO> recentApprovedColleges;
    private java.util.List<DepartmentDTO> recentDepartments;
    private java.util.List<BrochureDTO> recentBrochures;
}