package com.test.permissionusesjwt.repository;

import com.test.permissionusesjwt.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, String> {
    Optional<Section> findLessonByName(String name);

    List<Section> findByCourseId (String id);
    
    // Lấy danh sách section theo courseId được sắp xếp theo index
    @Query("SELECT s FROM Section s WHERE s.course.id = :courseId ORDER BY s.index")
    List<Section> findByCourseIdOrderByIndex(@Param("courseId") String courseId);
}
