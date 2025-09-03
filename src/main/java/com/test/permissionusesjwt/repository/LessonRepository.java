package com.test.permissionusesjwt.repository;

import com.test.permissionusesjwt.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository <Lesson, String> {
    List<Lesson> getLessonBySectionId(String sectionId);

    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.section.course.id = :courseId")
    long countLessonByCourseId(@Param("courseId") String courseId);
    
    // Đếm số bài học theo section
    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.section.id = :sectionId")
    long countLessonBySectionId(@Param("sectionId") String sectionId);
    
    // Lấy danh sách bài học theo section được sắp xếp theo thứ tự
    @Query("SELECT l FROM Lesson l WHERE l.section.id = :sectionId ORDER BY l.index")
    List<Lesson> findLessonsBySectionIdOrderByIndex(@Param("sectionId") String sectionId);
    
    // Lấy danh sách tất cả bài học trong khóa học được sắp xếp theo section và lesson index
    @Query("SELECT l FROM Lesson l WHERE l.section.course.id = :courseId ORDER BY l.section.index, l.index")
    List<Lesson> findLessonsByCourseIdOrderBySectionAndIndex(@Param("courseId") String courseId);
}
