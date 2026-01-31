package com.symbohub.symbohub_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrochureUploadResult {
    private BrochureDTO brochure;
    private List<EmailHistoryDTO> emailHistory;
    private int totalEmailsSent;
}
