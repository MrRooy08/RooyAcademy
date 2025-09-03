package com.test.permissionusesjwt.repository;

import com.test.permissionusesjwt.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, String> {
    
    List<Assignment> findBySectionId(String sectionId);
    
    @Query("SELECT COUNT(a) FROM Assignment a WHERE a.section.course.id = :courseId")
    long countAssignmentByCourseId(@Param("courseId") String courseId);
    
    @Query("SELECT COUNT(a) FROM Assignment a WHERE a.section.id = :sectionId")
    long countAssignmentBySectionId(@Param("sectionId") String sectionId);
    
    @Query("SELECT a FROM Assignment a WHERE a.section.id = :sectionId ORDER BY a.index")
    List<Assignment> findAssignmentsBySectionIdOrderByIndex(@Param("sectionId") String sectionId);
    
    @Query("SELECT a FROM Assignment a WHERE a.section.course.id = :courseId ORDER BY a.section.index, a.index")
    List<Assignment> findAssignmentsByCourseIdOrderBySectionAndIndex(@Param("courseId") String courseId);
    
    @Query("SELECT a FROM Assignment a WHERE a.instructor.id = :instructorId")
    List<Assignment> findByInstructorId(@Param("instructorId") String instructorId);
    
    @Query("SELECT COUNT(a) FROM Assignment a WHERE a.section.course.id = :courseId")
    int countByCourseId(@Param("courseId") String courseId);
} 