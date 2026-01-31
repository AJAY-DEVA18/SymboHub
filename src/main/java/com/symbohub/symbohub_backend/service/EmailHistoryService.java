package com.symbohub.symbohub_backend.service;



import com.symbohub.symbohub_backend.dto.EmailHistoryDTO;
import com.symbohub.symbohub_backend.entity.Brochure;
import com.symbohub.symbohub_backend.entity.Department;
import com.symbohub.symbohub_backend.entity.EmailHistory;
import com.symbohub.symbohub_backend.repository.EmailHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailHistoryService {

    private final EmailHistoryRepository emailHistoryRepository;

    @Transactional
    public EmailHistory recordEmailSent(Brochure brochure, Department department,
                                        String receiverEmail, String subject) {
        EmailHistory emailHistory = EmailHistory.builder()
                .brochure(brochure)
                .brochureTitle(brochure.getTitle())
                .department(department)
                .receiverEmail(receiverEmail)
                .subject(subject)
                .status(EmailHistory.EmailStatus.SENT)
                .sentDate(LocalDateTime.now())
                .build();

        return emailHistoryRepository.save(emailHistory);
    }

    @Transactional
    public void recordEmailDelivered(Long emailHistoryId) {
        EmailHistory emailHistory = emailHistoryRepository.findById(emailHistoryId)
                .orElseThrow(() -> new RuntimeException("Email history not found"));

        emailHistory.setStatus(EmailHistory.EmailStatus.DELIVERED);
        emailHistory.setDeliveredDate(LocalDateTime.now());
        emailHistoryRepository.save(emailHistory);
    }

    @Transactional
    public void recordEmailRead(Long emailHistoryId) {
        EmailHistory emailHistory = emailHistoryRepository.findById(emailHistoryId)
                .orElseThrow(() -> new RuntimeException("Email history not found"));

        emailHistory.setStatus(EmailHistory.EmailStatus.READ);
        emailHistory.setReadDate(LocalDateTime.now());
        emailHistoryRepository.save(emailHistory);
    }

    @Transactional
    public void recordEmailFailed(Long emailHistoryId, String errorMessage) {
        EmailHistory emailHistory = emailHistoryRepository.findById(emailHistoryId)
                .orElseThrow(() -> new RuntimeException("Email history not found"));

        emailHistory.setStatus(EmailHistory.EmailStatus.FAILED);
        emailHistory.setErrorMessage(errorMessage);
        emailHistoryRepository.save(emailHistory);
    }

    public List<EmailHistoryDTO> getDepartmentEmailHistory(Long departmentId) {
        return emailHistoryRepository.findByDepartmentId(departmentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EmailHistoryDTO> getBrochureEmailHistory(Long brochureId) {
        return emailHistoryRepository.findByBrochureId(brochureId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public EmailHistoryDTO convertToDTO(EmailHistory emailHistory) {
        EmailHistoryDTO dto = new EmailHistoryDTO();
        dto.setId(emailHistory.getId());
        dto.setBrochureTitle(emailHistory.getBrochureTitle());
        dto.setBrochureId(emailHistory.getBrochure() != null ? emailHistory.getBrochure().getId() : null);
        dto.setDepartmentId(emailHistory.getDepartment() != null ? emailHistory.getDepartment().getId() : null);
        dto.setReceiverEmail(emailHistory.getReceiverEmail());
        dto.setSubject(emailHistory.getSubject());
        dto.setStatus(emailHistory.getStatus().name());
        dto.setSentDate(formatDateTime(emailHistory.getSentDate()));
        dto.setDeliveredDate(formatDateTime(emailHistory.getDeliveredDate()));
        dto.setReadDate(formatDateTime(emailHistory.getReadDate()));
        dto.setErrorMessage(emailHistory.getErrorMessage());
        return dto;
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}