package com.test.permissionusesjwt.repository;

import com.test.permissionusesjwt.entity.CompletedAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompletedAssignmentRepository extends JpaRepository<CompletedAssignment, String> {
    
    @Query("SELECT ca FROM CompletedAssignment ca WHERE ca.progress.id = :progressId AND ca.assignment.id = :assignmentId")
    Optional<CompletedAssignment> findByProgressIdAndAssignmentId(@Param("progressId") String progressId, @Param("assignmentId") String assignmentId);
    
    @Query("SELECT ca FROM CompletedAssignment ca WHERE ca.progress.id = :progressId")
    List<CompletedAssignment> findByProgressId(@Param("progressId") String progressId);
    
    @Query("SELECT ca FROM CompletedAssignment ca WHERE ca.assignment.id = :assignmentId")
    List<CompletedAssignment> findByAssignmentId(@Param("assignmentId") String assignmentId);
    
    @Query("SELECT COUNT(ca) FROM CompletedAssignment ca WHERE ca.assignment.id = :assignmentId")
    long countByAssignmentId(@Param("assignmentId") String assignmentId);
    
    @Query("SELECT ca FROM CompletedAssignment ca WHERE ca.assignment.section.course.id = :courseId")
    List<CompletedAssignment> findByCourseId(@Param("courseId") String courseId);
    
    // Thêm method để đếm completed assignments theo progress
    @Query("SELECT COUNT(ca) FROM CompletedAssignment ca WHERE ca.progress.id = :progressId")
    long countByProgressId(@Param("progressId") String progressId);
    
    // Thêm method để đếm completed assignments theo course
    @Query("SELECT COUNT(ca) FROM CompletedAssignment ca WHERE ca.assignment.section.course.id = :courseId")
    long countByCourseId(@Param("courseId") String courseId);
    
    // Thêm method để đếm completed assignments theo section
    @Query("SELECT COUNT(ca) FROM CompletedAssignment ca WHERE ca.progress.id = :progressId AND ca.assignment.section.id = :sectionId")
    long countByProgressIdAndSectionId(@Param("progressId") String progressId, @Param("sectionId") String sectionId);
}