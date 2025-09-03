package com.test.permissionusesjwt.repository;

import com.test.permissionusesjwt.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssignmentAnswerRepository extends JpaRepository<Answer, String> {
    
    @Query("SELECT a FROM Answer a WHERE a.question.id = :questionId")
    Optional<Answer> findByQuestionId(@Param("questionId") String questionId);
} 