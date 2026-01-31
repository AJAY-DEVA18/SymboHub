package com.symbohub.symbohub_backend.repository;

import com.symbohub.symbohub_backend.entity.College;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollegeRepository extends JpaRepository<College, Long> {

    Optional<College> findByEmail(String email);

    Optional<College> findByName(String name);

    List<College> findByStatus(College.CollegeStatus status);

    @Query("SELECT c FROM College c WHERE c.status = 'APPROVED' AND c.isActive = true ORDER BY c.name")
    List<College> findAllApprovedColleges();

    @Query("SELECT c FROM College c WHERE c.status = 'PENDING' ORDER BY c.registrationDate DESC")
    List<College> findAllPendingColleges();

    @Query("SELECT COUNT(c) FROM College c WHERE c.status = 'APPROVED' AND c.isActive = true")
    long countApprovedColleges();

    @Query("SELECT COUNT(c) FROM College c WHERE c.status = 'PENDING'")
    long countPendingColleges();

    boolean existsByEmail(String email);

    boolean existsByName(String name);

    @Query("SELECT c FROM College c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<College> searchColleges(@Param("keyword") String keyword);
}