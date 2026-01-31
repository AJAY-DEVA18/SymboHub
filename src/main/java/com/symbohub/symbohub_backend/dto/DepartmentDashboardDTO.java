package com.symbohub.symbohub_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDashboardDTO {
    private DepartmentDTO department;
    private DashboardStats stats;
    private java.util.List<BrochureDTO> recentBrochures;
    private java.util.List<EmailHistoryDTO> recentEmails;
}