package com.symbohub.symbohub_backend.repository;

import com.symbohub.symbohub_backend.entity.Brochure;
import com.symbohub.symbohub_backend.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BrochureRepository extends JpaRepository<Brochure, Long> {

    List<Brochure> findByDepartment(Department department);

    @Query("SELECT b FROM Brochure b WHERE b.department.college.id = :collegeId")
    List<Brochure> findByCollegeId(@Param("collegeId") Long collegeId);

    @Query("SELECT b FROM Brochure b WHERE b.department.id = :departmentId " +
            "ORDER BY b.uploadDate DESC")
    List<Brochure> findByDepartmentId(@Param("departmentId") Long departmentId);

    @Query("SELECT b FROM Brochure b WHERE b.department.college.id = :collegeId " +
            "AND b.isPublic = true ORDER BY b.uploadDate DESC")
    List<Brochure> findPublicBrochuresByCollegeId(@Param("collegeId") Long collegeId);

    @Query("SELECT b FROM Brochure b WHERE b.department.id = :departmentId " +
            "AND b.isPublic = true ORDER BY b.uploadDate DESC")
    List<Brochure> findPublicBrochuresByDepartmentId(@Param("departmentId") Long departmentId);

    @Query("SELECT COUNT(b) FROM Brochure b WHERE b.department.id = :departmentId")
    long countByDepartmentId(@Param("departmentId") Long departmentId);

    @Query("SELECT b FROM Brochure b WHERE b.uploadDate BETWEEN :startDate AND :endDate")
    List<Brochure> findByUploadDateBetween(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(b) FROM Brochure b WHERE b.department.college.id = :collegeId")
    long countByCollegeId(@Param("collegeId") Long collegeId);
}