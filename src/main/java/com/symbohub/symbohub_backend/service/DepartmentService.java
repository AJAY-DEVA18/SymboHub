package com.symbohub.symbohub_backend.service;

import com.symbohub.symbohub_backend.dto.*;
import com.symbohub.symbohub_backend.entity.College;
import com.symbohub.symbohub_backend.entity.Department;
import com.symbohub.symbohub_backend.exception.DuplicateResourceException;
import com.symbohub.symbohub_backend.exception.ResourceNotFoundException;
import com.symbohub.symbohub_backend.repository.CollegeRepository;
import com.symbohub.symbohub_backend.repository.DepartmentRepository;
import com.symbohub.symbohub_backend.service.BrochureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final CollegeRepository collegeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final BrochureService brochureService;
    private final EmailHistoryService emailHistoryService;

    @Transactional
    public DepartmentDTO createDepartment(CreateDepartmentDTO departmentDTO) {
        // Check if department email already exists
        if (departmentRepository.existsByEmail(departmentDTO.getEmail())) {
            throw new DuplicateResourceException("Department with this email already exists");
        }

        // Find college
        College college = collegeRepository.findById(departmentDTO.getCollegeId())
                .orElseThrow(() -> new ResourceNotFoundException("College not found"));

        if (college.getStatus() != College.CollegeStatus.APPROVED) {
            throw new IllegalStateException("College is not approved. Cannot add departments.");
        }

        // Create department
        Department department = Department.builder()
                .name(departmentDTO.getName())
                .email(departmentDTO.getEmail())
                .password(passwordEncoder.encode(departmentDTO.getPassword()))
                .college(college)
                .createdAt(LocalDateTime.now())
                .isActive(true)
                .build();

        Department savedDepartment = departmentRepository.save(department);
        log.info("Department created successfully: {}", savedDepartment.getEmail());

        return convertToDTO(savedDepartment);
    }

    public LoginResponse loginDepartment(DepartmentLoginDTO loginDTO) {
        Department department = departmentRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        if (!passwordEncoder.matches(loginDTO.getPassword(), department.getPassword())) {
            throw new ResourceNotFoundException("Invalid credentials");
        }

        if (!department.getIsActive()) {
            throw new IllegalStateException("Department account is deactivated");
        }

        if (department.getCollege().getStatus() != College.CollegeStatus.APPROVED) {
            throw new IllegalStateException("College is not approved");
        }

        String token = jwtService.generateToken(department.getEmail(), "DEPARTMENT");

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setId(department.getId());
        response.setEmail(department.getEmail());
        response.setName(department.getName());
        response.setRole("DEPARTMENT");
        response.setCollegeId(department.getCollege().getId());
        response.setDepartmentId(department.getId());

        return response;
    }

    public DepartmentDashboardDTO getDepartmentDashboard(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        DashboardStats stats = new DashboardStats();
        stats.setTotalBrochures(department.getBrochures().size());
        stats.setTotalEmailsSent(department.getEmailHistory().size());

        List<BrochureDTO> recentBrochures = department.getBrochures().stream()
                .sorted((b1, b2) -> b2.getUploadDate().compareTo(b1.getUploadDate()))
                .limit(5)
                .map(this::convertBrochureToDTO)
                .collect(Collectors.toList());

        List<EmailHistoryDTO> recentEmails = department.getEmailHistory().stream()
                .sorted((e1, e2) -> e2.getSentDate().compareTo(e1.getSentDate()))
                .limit(5)
                .map(this::convertEmailHistoryToDTO)
                .collect(Collectors.toList());

        DepartmentDashboardDTO dashboard = new DepartmentDashboardDTO();
        dashboard.setDepartment(convertToDTO(department));
        dashboard.setStats(stats);
        dashboard.setRecentBrochures(recentBrochures);
        dashboard.setRecentEmails(recentEmails);

        return dashboard;
    }

    public List<DepartmentDTO> getCollegeDepartments(Long collegeId) {
        return departmentRepository.findByCollegeId(collegeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public DepartmentDTO getDepartmentById(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        return convertToDTO(department);
    }

    @Transactional
    public void updateDepartment(Long departmentId, CreateDepartmentDTO updateDTO) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        if (updateDTO.getName() != null) {
            department.setName(updateDTO.getName());
        }
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(department.getEmail())) {
            if (departmentRepository.existsByEmail(updateDTO.getEmail())) {
                throw new DuplicateResourceException("Email already in use");
            }
            department.setEmail(updateDTO.getEmail());
        }
        if (updateDTO.getPassword() != null) {
            department.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
        }

        departmentRepository.save(department);
    }

    @Transactional
    public void deactivateDepartment(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        department.setIsActive(false);
        departmentRepository.save(department);
    }

    @Transactional
    public void deleteDepartment(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        department.setIsActive(false);
        departmentRepository.save(department);
    }

    private DepartmentDTO convertToDTO(Department department) {
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
        return brochureService.convertToDTO(brochure);
    }

    private EmailHistoryDTO convertEmailHistoryToDTO(com.symbohub.symbohub_backend.entity.EmailHistory emailHistory) {
        return emailHistoryService.convertToDTO(emailHistory);
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}