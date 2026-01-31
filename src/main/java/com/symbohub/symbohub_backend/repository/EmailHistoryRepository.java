package com.symbohub.symbohub_backend.repository;

import com.symbohub.symbohub_backend.entity.Department;
import com.symbohub.symbohub_backend.entity.EmailHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmailHistoryRepository extends JpaRepository<EmailHistory, Long> {

    List<EmailHistory> findByDepartment(Department department);

    @Query("SELECT eh FROM EmailHistory eh WHERE eh.department.id = :departmentId " +
            "ORDER BY eh.sentDate DESC")
    List<EmailHistory> findByDepartmentId(@Param("departmentId") Long departmentId);

    @Query("SELECT eh FROM EmailHistory eh WHERE eh.brochure.id = :brochureId")
    List<EmailHistory> findByBrochureId(@Param("brochureId") Long brochureId);

    @Query("SELECT COUNT(eh) FROM EmailHistory eh WHERE eh.department.id = :departmentId " +
            "AND eh.status = 'DELIVERED'")
    long countDeliveredEmails(@Param("departmentId") Long departmentId);

    @Query("SELECT COUNT(eh) FROM EmailHistory eh WHERE eh.department.id = :departmentId " +
            "AND eh.status = 'READ'")
    long countReadEmails(@Param("departmentId") Long departmentId);

    @Query("SELECT eh FROM EmailHistory eh WHERE eh.sentDate BETWEEN :startDate AND :endDate")
    List<EmailHistory> findBySentDateBetween(@Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(eh) FROM EmailHistory eh WHERE eh.department.id = :departmentId")
    long countByDepartmentId(@Param("departmentId") Long departmentId);
}