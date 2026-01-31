package com.symbohub.symbohub_backend.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentStatusDTO {
    private int totalBrochures;
    private int totalEmailsSent;
    private int deliveredEmails;
    private int readEmails;
}

