package com.symbohub.symbohub_backend.service;



import com.symbohub.symbohub_backend.dto.*;
import com.symbohub.symbohub_backend.entity.Admin;
import com.symbohub.symbohub_backend.entity.College;
import com.symbohub.symbohub_backend.entity.Department;
import com.symbohub.symbohub_backend.exception.ResourceNotFoundException;
import com.symbohub.symbohub_backend.repository.AdminRepository;
import com.symbohub.symbohub_backend.repository.CollegeRepository;
import com.symbohub.symbohub_backend.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final AdminRepository adminRepository;
    private final CollegeRepository collegeRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CollegeService collegeService;

    public LoginResponse login(AdminLoginRequest loginDTO) {
        Admin admin = adminRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        if (!passwordEncoder.matches(loginDTO.getPassword(), admin.getPassword())) {
            throw new ResourceNotFoundException("Invalid credentials");
        }

        if (!admin.getIsActive()) {
            throw new IllegalStateException("Admin account is deactivated");
        }

        // Update last login
        admin.setLastLogin(LocalDateTime.now());
        adminRepository.save(admin);

        String token = jwtService.generateToken(admin.getEmail(), "ADMIN");

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setId(admin.getId());
        response.setEmail(admin.getEmail());
        response.setName(admin.getFullName());
        response.setRole(admin.getRole().name());

        return response;
    }

    public AdminDashboardDTO getDashboard() {
        DashboardStats stats = new DashboardStats();
        stats.setTotalColleges(collegeRepository.count());
        stats.setApprovedColleges(collegeRepository.countApprovedColleges());
        stats.setPendingColleges(collegeRepository.countPendingColleges());
        stats.setTotalDepartments(departmentRepository.count());
        stats.setTotalBrochures(collegeRepository.findAll().stream()
                .flatMap(c -> c.getDepartments().stream())
                .mapToLong(d -> d.getBrochures().size())
                .sum());
        stats.setTotalEmailsSent(collegeRepository.findAll().stream()
                .flatMap(c -> c.getDepartments().stream())
                .mapToLong(d -> d.getEmailHistory().size())
                .sum());

        List<CollegeDTO> recentPendingColleges = collegeRepository.findAllPendingColleges().stream()
                .limit(5)
                .map(collegeService::convertToDTO)
                .collect(Collectors.toList());

        List<CollegeDTO> recentApprovedColleges = collegeRepository.findAllApprovedColleges().stream()
                .limit(5)
                .map(collegeService::convertToDTO)
                .collect(Collectors.toList());

        List<DepartmentDTO> recentDepartments = departmentRepository.findAllActiveDepartments().stream()
                .sorted((d1, d2) -> d2.getCreatedAt().compareTo(d1.getCreatedAt()))
                .limit(5)
                .map(this::convertDepartmentToDTO)
                .collect(Collectors.toList());

        AdminDashboardDTO dashboard = new AdminDashboardDTO();
        dashboard.setStats(stats);
        dashboard.setRecentPendingColleges(recentPendingColleges);
        dashboard.setRecentApprovedColleges(recentApprovedColleges);
        dashboard.setRecentDepartments(recentDepartments);

        return dashboard;
    }

    public List<CollegeDTO> getPendingColleges() {
        return collegeRepository.findAllPendingColleges().stream()
                .map(collegeService::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CollegeDTO> getApprovedColleges() {
        return collegeRepository.findAllApprovedColleges().stream()
                .map(collegeService::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CollegeDTO approveCollege(Long collegeId) {
        return collegeService.approveCollege(collegeId);
    }

    @Transactional
    public CollegeDTO rejectCollege(Long collegeId, String rejectionReason) {
        CollegeDTO rejectedCollege = collegeService.rejectCollege(collegeId, rejectionReason);

        // Send rejection email
        // emailService.sendCollegeRejectionNotification(rejectedCollege, rejectionReason);

        return rejectedCollege;
    }

    @Transactional
    public void deleteCollege(Long collegeId) {
        collegeService.deleteCollege(collegeId);
    }

    @Transactional
    public void deleteDepartment(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        department.setIsActive(false);
        departmentRepository.save(department);
    }

    public void validateAdminToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new SecurityException("Invalid token");
        }

        String jwt = token.substring(7);
        String email = jwtService.extractUsername(jwt);

        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        if (!jwtService.isTokenValid(jwt, admin.getEmail())) {
            throw new SecurityException("Invalid token");
        }
    }

    @Transactional
    public Admin createAdmin(Admin admin) {
        if (adminRepository.existsByUsername(admin.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (adminRepository.existsByEmail(admin.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setCreatedAt(LocalDateTime.now());
        admin.setIsActive(true);

        return adminRepository.save(admin);
    }

    private DepartmentDTO convertDepartmentToDTO(Department department) {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setEmail(department.getEmail());
        dto.setCollegeId(department.getCollege().getId());
        dto.setCollegeName(department.getCollege().getName());
        dto.setCreatedAt(department.getCreatedAt().toString());
        dto.setBrochureCount(department.getBrochures().size());
        dto.setEmailCount(department.getEmailHistory().size());
        dto.setActive(department.getIsActive());
        return dto;
    }
}