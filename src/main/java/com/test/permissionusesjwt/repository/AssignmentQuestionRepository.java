package com.test.permissionusesjwt.repository;

import com.test.permissionusesjwt.entity.AssignmentQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentQuestionRepository extends JpaRepository<AssignmentQuestion, String> {
    
    @Query("SELECT q FROM AssignmentQuestion q WHERE q.assignment.id = :assignmentId ORDER BY q.index")
    List<AssignmentQuestion> findQuestionsByAssignmentIdOrderByIndex(@Param("assignmentId") String assignmentId);
    
    @Query("SELECT COUNT(q) FROM AssignmentQuestion q WHERE q.assignment.id = :assignmentId")
    int countQuestionsByAssignmentId(@Param("assignmentId") String assignmentId);

    List<AssignmentQuestion> findByAssignmentId(String s);
}