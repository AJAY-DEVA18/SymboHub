package com.symbohub.symbohub_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrochureDistributionDTO {
    private Long brochureId;
    private List<DepartmentDTO> targetDepartments;
    private String previewHtml;
}
