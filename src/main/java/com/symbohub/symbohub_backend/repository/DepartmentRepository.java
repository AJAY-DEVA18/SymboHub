package com.symbohub.symbohub_backend.repository;

import com.symbohub.symbohub_backend.entity.College;
import com.symbohub.symbohub_backend.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByEmail(String email);

    List<Department> findByCollege(College college);

    @Query("SELECT d FROM Department d WHERE d.college.id = :collegeId AND d.isActive = true")
    List<Department> findByCollegeId(@Param("collegeId") Long collegeId);

    @Query("SELECT d FROM Department d WHERE d.college.id = :collegeId AND LOWER(d.name) " +
            "LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Department> searchInCollege(@Param("collegeId") Long collegeId,
                                     @Param("keyword") String keyword);

    boolean existsByEmail(String email);

    @Query("SELECT COUNT(d) FROM Department d WHERE d.college.id = :collegeId")
    long countByCollegeId(@Param("collegeId") Long collegeId);

    @Query("SELECT d FROM Department d WHERE d.isActive = true AND d.college.status = 'APPROVED'")
    List<Department> findAllActiveDepartments();
}