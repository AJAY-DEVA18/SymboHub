package com.symbohub.symbohub_backend.config;

import com.symbohub.symbohub_backend.entity.Admin;
import com.symbohub.symbohub_backend.entity.College;
import com.symbohub.symbohub_backend.entity.Department;
import com.symbohub.symbohub_backend.repository.AdminRepository;
import com.symbohub.symbohub_backend.repository.CollegeRepository;
import com.symbohub.symbohub_backend.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final CollegeRepository collegeRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        try {
            // Create admin user
            if (adminRepository.count() == 0) {
                Admin admin = Admin.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .email("admin@symbohub.com")
                        .fullName("System Administrator")
                        .role(Admin.Role.SUPER_ADMIN)
                        .createdAt(LocalDateTime.now())
                        .isActive(true)
                        .build();
                adminRepository.save(admin);
                log.info("Admin user created: admin/admin123");
            }

            // Create sample approved college
            if (collegeRepository.count() == 0) {
                College sampleCollege = College.builder()
                        .name("American College")
                        .email("admin@american.edu")
                        .password(passwordEncoder.encode("1234"))
                        .address("456 College Ave, Chennai")
                        .status(College.CollegeStatus.APPROVED)
                        .registrationDate(LocalDateTime.now().minusDays(10))
                        .approvalDate(LocalDateTime.now().minusDays(8))
                        .isActive(true)
                        .build();

                College savedCollege = collegeRepository.save(sampleCollege);
                log.info("Sample college created: American College");

                // Create sample departments
                Department csDept = Department.builder()
                        .name("Computer Science")
                        .email("cs@american.edu")
                        .password(passwordEncoder.encode("cs123"))
                        .college(savedCollege)
                        .createdAt(LocalDateTime.now().minusDays(7))
                        .isActive(true)
                        .build();

                Department eeDept = Department.builder()
                        .name("Electrical Engineering")
                        .email("ee@american.edu")
                        .password(passwordEncoder.encode("ee123"))
                        .college(savedCollege)
                        .createdAt(LocalDateTime.now().minusDays(6))
                        .isActive(true)
                        .build();

                departmentRepository.save(csDept);
                departmentRepository.save(eeDept);
                log.info("Sample departments created");
            }

            log.info("✅ Data initialization completed successfully");
        } catch (Exception e) {
            log.error("❌ Data initialization failed: ", e);
        }
    }
}