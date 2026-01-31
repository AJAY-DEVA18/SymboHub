package com.symbohub.symbohub_backend.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailHistoryDTO {
    private Long id;
    private String brochureTitle;
    private Long brochureId;
    private Long departmentId;
    private String receiverEmail;
    private String subject;
    private String status;
    private String sentDate;
    private String deliveredDate;
    private String readDate;
    private String errorMessage;
}