package com.symbohub.symbohub_backend.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {
    private Long id;
    private String name;
    private String email;
    private Long collegeId;
    private String collegeName;
    private String createdAt;
    private int brochureCount;
    private int emailCount;
    private boolean isActive;
}