package com.test.permissionusesjwt.repository;

import com.test.permissionusesjwt.entity.CompletedLesson;
import com.test.permissionusesjwt.entity.CompletedLessonId;
import com.test.permissionusesjwt.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import com.test.permissionusesjwt.entity.Lesson;

@Repository
public interface CompletedLessonRepository extends JpaRepository<CompletedLesson, CompletedLessonId> {
    long countByProgressId(String progressId);
    
    // Lấy danh sách bài học đã hoàn thành theo progress
    List<CompletedLesson> findByProgressId(String progressId);
    
    // Đếm số bài học đã hoàn thành theo section
    @Query("SELECT COUNT(cl) FROM CompletedLesson cl WHERE cl.progress.id = :progressId AND cl.lesson.section.id = :sectionId")
    long countCompletedLessonsByProgressAndSection(@Param("progressId") String progressId, @Param("sectionId") String sectionId);
    
    // Lấy danh sách bài học đã hoàn thành theo section
    @Query("SELECT cl FROM CompletedLesson cl WHERE cl.progress.id = :progressId AND cl.lesson.section.id = :sectionId ORDER BY cl.lesson.index")
    List<CompletedLesson> findCompletedLessonsByProgressAndSection(@Param("progressId") String progressId, @Param("sectionId") String sectionId);
    
    // Kiểm tra bài học đã hoàn thành hay chưa
    boolean existsByProgressIdAndLessonId(String progressId, String lessonId);
    

    
    // Lấy bài học đầu tiên chưa hoàn thành
    @Query("SELECT l FROM Lesson l " +
           "WHERE l.section.course.id = (SELECT p.enrollment.course.id FROM Progress p WHERE p.id = :progressId) " +
           "AND l.id NOT IN (SELECT cl.lesson.id FROM CompletedLesson cl WHERE cl.progress.id = :progressId) " +
           "ORDER BY l.section.index, l.index")
    List<Lesson> findNextLessonToCompleteList(@Param("progressId") String progressId);
    
    // Helper method để lấy bài học đầu tiên
    default Lesson findNextLessonToComplete(String progressId) {
        List<Lesson> lessons = findNextLessonToCompleteList(progressId);
        return lessons.isEmpty() ? null : lessons.get(0);
    }
}
