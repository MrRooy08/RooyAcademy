package com.test.permissionusesjwt.repository;

import com.test.permissionusesjwt.entity.AnswerStudent;
import com.test.permissionusesjwt.entity.AnswerStudentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerStudentRepository extends JpaRepository<AnswerStudent, AnswerStudentId> {
    
    /**
     * Lấy tất cả câu trả lời của học viên cho một bài tập đã hoàn thành
     */
    @Query("SELECT a FROM AnswerStudent a WHERE a.id.completedAssignmentId = :completedAssignmentId ORDER BY a.question.index")
    List<AnswerStudent> findByCompletedAssignmentId(@Param("completedAssignmentId") String completedAssignmentId);
    
    /**
     * Lấy câu trả lời của học viên cho một câu hỏi cụ thể trong một bài tập đã hoàn thành
     */
    @Query("SELECT a FROM AnswerStudent a WHERE a.id.completedAssignmentId = :completedAssignmentId AND a.id.questionId = :questionId")
    AnswerStudent findByCompletedAssignmentIdAndQuestionId(@Param("completedAssignmentId") String completedAssignmentId, 
                                                          @Param("questionId") String questionId);
    
    /**
     * Kiểm tra xem học viên đã trả lời câu hỏi này chưa
     */
    boolean existsById_CompletedAssignmentIdAndId_QuestionId(String completedAssignmentId, String questionId);
    
    /**
     * Tìm tất cả câu trả lời theo completedAssignmentId
     */
    List<AnswerStudent> findById_CompletedAssignmentId(String completedAssignmentId);
} 