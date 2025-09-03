package com.test.permissionusesjwt.service;

import com.test.permissionusesjwt.authUtils.AuthUtils;
import com.test.permissionusesjwt.dto.request.AssignmentQuestionRequest;
import com.test.permissionusesjwt.dto.response.AssignmentQuestionResponse;
import com.test.permissionusesjwt.entity.*;
import com.test.permissionusesjwt.exception.AppException;
import com.test.permissionusesjwt.exception.ErrorCode;
import com.test.permissionusesjwt.mapper.AssignmentQuestionMapper;
import com.test.permissionusesjwt.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AssignmentQuestionService {
    
    AssignmentQuestionRepository assignmentQuestionRepository;
    AssignmentAnswerRepository assignmentAnswerRepository;
    AssignmentRepository assignmentRepository;
    UserRepository userRepository;
    InstructorRepository instructorRepository;
    AssignmentQuestionMapper assignmentQuestionMapper;
    AuthUtils authUtils;

    @Transactional
    public AssignmentQuestionResponse createQuestion(String assignmentId, AssignmentQuestionRequest request) {
        // Kiểm tra quyền tạo câu hỏi (chỉ giảng viên)
        String currentUsername = authUtils.getCurrentUsername();
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        
        Instructor instructor = instructorRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.INSTRUCTOR_NOT_FOUND));

        // Kiểm tra bài tập tồn tại
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        // Kiểm tra giảng viên có quyền chỉnh sửa bài tập này không
        if (!assignment.getInstructor().getId().equals(instructor.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Tạo câu hỏi
        AssignmentQuestion question = assignmentQuestionMapper.toAssignmentQuestion(request);
        question.setAssignment(assignment);
        question = assignmentQuestionRepository.save(question);

        // Tạo đáp án chuẩn nếu có
        if (request.getAnswerContent() != null && !request.getAnswerContent().trim().isEmpty()) {
            Answer answer = new Answer();
            answer.setAnswerContent(request.getAnswerContent());
            answer.setQuestion(question);
            assignmentAnswerRepository.save(answer);
        }

        return assignmentQuestionMapper.toAssignmentQuestionResponse(question);
    }

    public List<AssignmentQuestionResponse> getQuestionsByAssignment(String assignmentId, boolean includeAnswer) {
        List<AssignmentQuestion> questions = assignmentQuestionRepository.findQuestionsByAssignmentIdOrderByIndex(assignmentId);
        
        return questions.stream()
                .map(question -> {
                    AssignmentQuestionResponse response = assignmentQuestionMapper.toAssignmentQuestionResponse(question);
                    
                    // Chỉ hiển thị đáp án cho giảng viên
                    if (includeAnswer && question.getAnswer() != null) {
                        return null;
                    }
                    
                    return response;
                })
                .collect(Collectors.toList());
    }

    public AssignmentQuestionResponse getQuestionById(String questionId, boolean includeAnswer) {
        AssignmentQuestion question = assignmentQuestionRepository.findById(questionId)
                .orElseThrow(() -> new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));
        
        AssignmentQuestionResponse response = assignmentQuestionMapper.toAssignmentQuestionResponse(question);
        
        // Chỉ hiển thị đáp án cho giảng viên
        if (includeAnswer && question.getAnswer() != null) {
            return null;
        }
        
        return response;
    }

    @Transactional
    public AssignmentQuestionResponse updateQuestion(String questionId, AssignmentQuestionRequest request) {
        // Kiểm tra quyền chỉnh sửa
        String currentUsername = authUtils.getCurrentUsername();
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        
        Instructor instructor = instructorRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.INSTRUCTOR_NOT_FOUND));

        AssignmentQuestion question = assignmentQuestionRepository.findById(questionId)
                .orElseThrow(() -> new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        // Kiểm tra giảng viên có quyền chỉnh sửa câu hỏi này không
        if (!question.getAssignment().getInstructor().getId().equals(instructor.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Cập nhật thông tin câu hỏi
        question.setQuestionContent(request.getQuestionContent());
        question.setIndex(request.getIndex());
        question = assignmentQuestionRepository.save(question);

        // Cập nhật đáp án nếu có
        if (request.getAnswerContent() != null && !request.getAnswerContent().trim().isEmpty()) {
            Answer existingAnswer = question.getAnswer();
            if (existingAnswer != null) {
                existingAnswer.setAnswerContent(request.getAnswerContent());
                assignmentAnswerRepository.save(existingAnswer);
            } else {
                Answer newAnswer = new Answer();
                newAnswer.setAnswerContent(request.getAnswerContent());
                newAnswer.setQuestion(question);
                assignmentAnswerRepository.save(newAnswer);
            }
        }

        return assignmentQuestionMapper.toAssignmentQuestionResponse(question);
    }

    @Transactional
    public void deleteQuestion(String questionId) {
        // Kiểm tra quyền xóa
        String currentUsername = authUtils.getCurrentUsername();
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        
        Instructor instructor = instructorRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.INSTRUCTOR_NOT_FOUND));

        AssignmentQuestion question = assignmentQuestionRepository.findById(questionId)
                .orElseThrow(() -> new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        // Kiểm tra giảng viên có quyền xóa câu hỏi này không
        if (!question.getAssignment().getInstructor().getId().equals(instructor.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        assignmentQuestionRepository.delete(question);
    }
} 