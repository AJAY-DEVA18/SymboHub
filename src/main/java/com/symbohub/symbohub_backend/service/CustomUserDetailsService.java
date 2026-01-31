package com.symbohub.symbohub_backend.security;

import com.symbohub.symbohub_backend.entity.Admin;
import com.symbohub.symbohub_backend.entity.College;
import com.symbohub.symbohub_backend.entity.Department;
import com.symbohub.symbohub_backend.repository.AdminRepository;
import com.symbohub.symbohub_backend.repository.CollegeRepository;
import com.symbohub.symbohub_backend.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final CollegeRepository collegeRepository;
    private final DepartmentRepository departmentRepository;
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Check if it's a college
        College college = collegeRepository.findByEmail(username).orElse(null);
        if (college != null) {
            return new User(
                    college.getEmail(),
                    college.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_COLLEGE"))
            );
        }

        // Check if it's a department
        Department department = departmentRepository.findByEmail(username).orElse(null);
        if (department != null) {
            return new User(
                    department.getEmail(),
                    department.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_DEPARTMENT"))
            );
        }

        // Check if it's an admin
        Admin admin = adminRepository.findByEmail(username).orElse(null);
        if (admin != null) {
            return new User(
                    admin.getEmail(),
                    admin.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }

        throw new UsernameNotFoundException("User not found with email: " + username);
    }
}