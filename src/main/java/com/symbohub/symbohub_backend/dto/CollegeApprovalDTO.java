package com.symbohub.symbohub_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollegeApprovalDTO {
    private Long collegeId;
    private String action; // APPROVE or REJECT
    private String rejectionReason;
}