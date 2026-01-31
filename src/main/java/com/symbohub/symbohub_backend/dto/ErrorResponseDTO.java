package com.symbohub.symbohub_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
class ErrorResponseDTO {
    private String error;
    private String message;
    private int status;
    private Long timestamp;
}
