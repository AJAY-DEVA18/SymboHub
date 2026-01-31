package com.symbohub.symbohub_backend.service;

import com.symbohub.symbohub_backend.dto.*;
import com.symbohub.symbohub_backend.entity.College;
import com.symbohub.symbohub_backend.entity.Department;
import com.symbohub.symbohub_backend.exception.DuplicateResourceException;
import com.symbohub.symbohub_backend.exception.ResourceNotFoundException;
import com.symbohub.symbohub_backend.repository.CollegeRepository;
import com.symbohub.symbohub_backend.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollegeService {

    private final CollegeRepository collegeRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;
    private final EmailService emailService;
    private final JwtService jwtService;

    @Transactional
    public CollegeDTO registerCollege(CollegeRegistrationDTO registrationDTO, MultipartFile staffIdFile) {
        // Check if college already exists
        if (collegeRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new DuplicateResourceException("College with this email already exists");
        }
        if (collegeRepository.existsByName(registrationDTO.getName())) {
            throw new DuplicateResourceException("College with this name already exists");
        }

        // Save staff ID file
        String staffIdPath = null;
        String staffIdFileName = null;
        if (staffIdFile != null && !staffIdFile.isEmpty()) {
            try {
                staffIdPath = fileStorageService.storeFile(staffIdFile, "staff-ids");
                staffIdFileName = staffIdFile.getOriginalFilename();
            } catch (RuntimeException e) {
                log.error("Failed to save staff ID file", e);
                throw new RuntimeException("Failed to save staff ID file");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Create college entity
        College college = College.builder()
                .name(registrationDTO.getName())
                .email(registrationDTO.getEmail())
                .password(passwordEncoder.encode(registrationDTO.getPassword()))
                .address(registrationDTO.getAddress())
                .staffIdPath(staffIdPath)
                .staffIdFileName(staffIdFileName)
                .status(College.CollegeStatus.PENDING)
                .registrationDate(LocalDateTime.now())
                .isActive(true)
                .build();

        College savedCollege = collegeRepository.save(college);
        log.info("College registered successfully: {}", savedCollege.getEmail());

        return convertToDTO(savedCollege);
    }

    public LoginResponse loginCollege(CollegeLoginDTO loginDTO) {
        College college = collegeRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("College not found"));

        if (!passwordEncoder.matches(loginDTO.getPassword(), college.getPassword())) {
            throw new ResourceNotFoundException("Invalid credentials");
        }

        if (college.getStatus() != College.CollegeStatus.APPROVED) {
            throw new IllegalStateException("College is not approved yet. Current status: " + college.getStatus());
        }

        if (!college.getIsActive()) {
            throw new IllegalStateException("College account is deactivated");
        }

        String token = jwtService.generateToken(college.getEmail(), "COLLEGE");

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setId(college.getId());
        response.setEmail(college.getEmail());
        response.setName(college.getName());
        response.setRole("COLLEGE");
        response.setCollegeId(college.getId());

        return response;
    }

    public List<CollegeDTO> getApprovedColleges() {
        return collegeRepository.findAllApprovedColleges().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CollegeDTO> getPendingColleges() {
        return collegeRepository.findAllPendingColleges().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CollegeDTO getCollegeById(Long collegeId) {
        College college = collegeRepository.findById(collegeId)
                .orElseThrow(() -> new ResourceNotFoundException("College not found with id: " + collegeId));
        return convertToDTO(college);
    }

    public CollegeDTO getCollegeByEmail(String email) {
        College college = collegeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("College not found with email: " + email));
        return convertToDTO(college);
    }

    public CollegeDashboardDTO getCollegeDashboard(Long collegeId) {
        College college = collegeRepository.findById(collegeId)
                .orElseThrow(() -> new ResourceNotFoundException("College not found"));

        List<Department> departments = departmentRepository.findByCollegeId(collegeId);

        DashboardStats stats = new DashboardStats();
        stats.setTotalDepartments(departments.size());
        stats.setTotalBrochures(departments.stream()
                .mapToLong(d -> d.getBrochures().size())
                .sum());
        stats.setTotalEmailsSent(departments.stream()
                .mapToLong(d -> d.getEmailHistory().size())
                .sum());

        List<DepartmentDTO> recentDepartments = departments.stream()
                .limit(5)
                .map(this::convertDepartmentToDTO)
                .collect(Collectors.toList());

        List<BrochureDTO> recentBrochures = departments.stream()
                .flatMap(d -> d.getBrochures().stream())
                .sorted((b1, b2) -> b2.getUploadDate().compareTo(b1.getUploadDate()))
                .limit(5)
                .map(this::convertBrochureToDTO)
                .collect(Collectors.toList());

        CollegeDashboardDTO dashboard = new CollegeDashboardDTO();
        dashboard.setCollege(convertToDTO(college));
        dashboard.setStats(stats);
        dashboard.setRecentDepartments(recentDepartments);
        dashboard.setRecentBrochures(recentBrochures);

        return dashboard;
    }

    @Transactional
    public CollegeDTO approveCollege(Long collegeId) {
        College college = collegeRepository.findById(collegeId)
                .orElseThrow(() -> new ResourceNotFoundException("College not found"));

        college.setStatus(College.CollegeStatus.APPROVED);
        college.setApprovalDate(LocalDateTime.now());
        College savedCollege = collegeRepository.save(college);

        // Send approval email
        emailService.sendCollegeApprovalNotification(convertToDTO(savedCollege));

        return convertToDTO(savedCollege);
    }

    @Transactional
    public CollegeDTO rejectCollege(Long collegeId, String rejectionReason) {
        College college = collegeRepository.findById(collegeId)
                .orElseThrow(() -> new ResourceNotFoundException("College not found"));

        college.setStatus(College.CollegeStatus.REJECTED);
        college.setRejectionReason(rejectionReason);
        college.setIsActive(false);
        College savedCollege = collegeRepository.save(college);

        return convertToDTO(savedCollege);
    }

    @Transactional
    public void deleteCollege(Long collegeId) {
        College college = collegeRepository.findById(collegeId)
                .orElseThrow(() -> new ResourceNotFoundException("College not found"));

        // Delete associated departments and brochures
        List<Department> departments = departmentRepository.findByCollegeId(collegeId);
        for (Department department : departments) {
            department.setIsActive(false);
            departmentRepository.save(department);
        }

        college.setIsActive(false);
        collegeRepository.save(college);
    }

    public CollegeDTO convertToDTO(College college) {
        CollegeDTO dto = new CollegeDTO();
        dto.setId(college.getId());
        dto.setName(college.getName());
        dto.setEmail(college.getEmail());
        dto.setAddress(college.getAddress());
        dto.setStatus(college.getStatus().name());
        dto.setRegistrationDate(formatDateTime(college.getRegistrationDate()));
        dto.setApprovalDate(formatDateTime(college.getApprovalDate()));
        dto.setStaffIdFileName(college.getStaffIdFileName());
        dto.setDepartmentCount(college.getDepartments().size());
        dto.setBrochureCount(college.getDepartments().stream()
                .mapToInt(d -> d.getBrochures().size())
                .sum());
        dto.setEmailCount(college.getDepartments().stream()
                .mapToInt(d -> d.getEmailHistory().size())
                .sum());
        dto.setActive(college.getIsActive());
        return dto;
    }

    private DepartmentDTO convertDepartmentToDTO(Department department) {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setEmail(department.getEmail());
        dto.setCollegeId(department.getCollege().getId());
        dto.setCollegeName(department.getCollege().getName());
        dto.setCreatedAt(formatDateTime(department.getCreatedAt()));
        dto.setBrochureCount(department.getBrochures().size());
        dto.setEmailCount(department.getEmailHistory().size());
        dto.setActive(department.getIsActive());
        return dto;
    }

    private BrochureDTO convertBrochureToDTO(com.symbohub.symbohub_backend.entity.Brochure brochure) {
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