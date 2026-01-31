package com.symbohub.symbohub_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollegeDTO {
    private Long id;
    private String name;
    private String email;
    private String address;
    private String status;
    private String registrationDate;
    private String approvalDate;
    private String staffIdFileName;
    private int departmentCount;
    private int brochureCount;
    private int emailCount;
    private boolean isActive;
}
