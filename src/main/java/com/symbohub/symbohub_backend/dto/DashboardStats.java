package com.symbohub.symbohub_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {
    private long totalColleges;
    private long approvedColleges;
    private long pendingColleges;
    private long totalDepartments;
    private long totalBrochures;
    private long totalEmailsSent;
}