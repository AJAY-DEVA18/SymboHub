package com.symbohub.symbohub_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrochureDTO {
    private Long id;
    private String title;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String uploadDate;
    private Long departmentId;
    private String departmentName;
    private Long collegeId;
    private String collegeName;
    private int emailSentCount;
    private String description;
    private boolean isPublic;
}

