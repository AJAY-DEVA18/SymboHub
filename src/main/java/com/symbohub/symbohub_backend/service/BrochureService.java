package com.symbohub.symbohub_backend.service;

import com.symbohub.symbohub_backend.dto.*;
import com.symbohub.symbohub_backend.entity.Brochure;
import com.symbohub.symbohub_backend.entity.Department;
import com.symbohub.symbohub_backend.exception.ResourceNotFoundException;
import com.symbohub.symbohub_backend.repository.BrochureRepository;
import com.symbohub.symbohub_backend.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BrochureService {

    private final BrochureRepository brochureRepository;
    private final DepartmentRepository departmentRepository;
    private final FileStorageService fileStorageService;
    private final EmailService emailService;
    private final EmailHistoryService emailHistoryService;

    @Transactional
    public BrochureUploadResult uploadBrochure(CreateBrochureDTO brochureDTO,
                                               MultipartFile file,
                                               Long departmentId) {
        // Find uploading department
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        // Save file
        String filePath;
        try {
            filePath = fileStorageService.storeFile(file, "brochures");
        } catch (RuntimeException e) {
            log.error("Failed to save brochure file", e);
            throw new RuntimeException("Failed to save brochure file");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Create brochure
        Brochure brochure = Brochure.builder()
                .title(brochureDTO.getTitle())
                .description(brochureDTO.getDescription())
                .filePath(filePath)
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .fileType(file.getContentType())
                .department(department)
                .uploadDate(LocalDateTime.now())
                .isPublic(brochureDTO.isPublic())
                .build();

        Brochure savedBrochure = brochureRepository.save(brochure);

        // Send emails to selected departments
        List<EmailHistoryDTO> emailHistory = new ArrayList<>();
        if (brochureDTO.getDepartmentIds() != null && !brochureDTO.getDepartmentIds().isEmpty()) {
            for (Long targetDeptId : brochureDTO.getDepartmentIds()) {
                if (!targetDeptId.equals(departmentId)) {
                    Department targetDept = departmentRepository.findById(targetDeptId)
                            .orElseThrow(() -> new ResourceNotFoundException("Target department not found"));

                    // Send email
                    emailService.sendBrochureNotification(
                            targetDept.getEmail(),
                            savedBrochure.getTitle(),
                            department.getName(),
                            savedBrochure.getDescription()
                    );

                    // Record email history
                    com.symbohub.symbohub_backend.entity.EmailHistory emailRecord =
                            emailHistoryService.recordEmailSent(
                                    savedBrochure,
                                    targetDept,
                                    targetDept.getEmail(),
                                    "New Brochure: " + savedBrochure.getTitle()
                            );

                    emailHistory.add(emailHistoryService.convertToDTO(emailRecord));
                }
            }
        }

        BrochureUploadResult result = new BrochureUploadResult();
        result.setBrochure(convertToDTO(savedBrochure));
        result.setEmailHistory(emailHistory);
        result.setTotalEmailsSent(emailHistory.size());

        return result;
    }

    public List<BrochureDTO> getDepartmentBrochures(Long departmentId) {
        return brochureRepository.findByDepartmentId(departmentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BrochureDTO> getPublicBrochuresByCollege(Long collegeId) {
        return brochureRepository.findPublicBrochuresByCollegeId(collegeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BrochureDTO> getPublicBrochuresByDepartment(Long departmentId) {
        return brochureRepository.findPublicBrochuresByDepartmentId(departmentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public BrochureDTO getBrochureById(Long brochureId) {
        Brochure brochure = brochureRepository.findById(brochureId)
                .orElseThrow(() -> new ResourceNotFoundException("Brochure not found"));
        return convertToDTO(brochure);
    }

    @Transactional
    public void deleteBrochure(Long brochureId) {
        Brochure brochure = brochureRepository.findById(brochureId)
                .orElseThrow(() -> new ResourceNotFoundException("Brochure not found"));

        // Delete file from storage
        if (brochure.getFilePath() != null) {
            try {
                fileStorageService.deleteFile(brochure.getFilePath());
            } catch (
                    RuntimeException e) {
                log.error("Failed to delete brochure file: {}", brochure.getFilePath(), e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        brochureRepository.delete(brochure);
    }

    public BrochureDTO convertToDTO(Brochure brochure) {
        BrochureDTO dto = new BrochureDTO();
        dto.setId(brochure.getId());
        dto.setTitle(brochure.getTitle());
        dto.setFileName(brochure.getFileName());
        dto.setFileType(brochure.getFileType());
        dto.setFileSize(brochure.getFileSize());
        dto.setUploadDate(formatDateTime(brochure.getUploadDate()));
        dto.setDepartmentId(brochure.getDepartment().getId());
        dto.setDepartmentName(brochure.getDepartment().getName());
        dto.setCollegeId(brochure.getDepartment().getCollege().getId());
        dto.setCollegeName(brochure.getDepartment().getCollege().getName());
        dto.setEmailSentCount(brochure.getEmailHistories().size());
        dto.setDescription(brochure.getDescription());
        dto.setPublic(brochure.getIsPublic());
        return dto;
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}