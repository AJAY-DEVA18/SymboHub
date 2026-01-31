package com.symbohub.symbohub_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CreateBrochureDTO {

    @NotBlank(message = "Brochure title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    private String description;
    private List<Long> departmentIds;
    private List<Long> collegeIds;
    private boolean isPublic = true;
}
