package com.test.permissionusesjwt.controller;

import com.test.permissionusesjwt.dto.request.*;
import com.test.permissionusesjwt.dto.response.AssignmentResponse;
import com.test.permissionusesjwt.dto.response.CompletedAssignmentResponse;
import com.test.permissionusesjwt.dto.response.CompletedAssignmentWithAnswersResponse;
import com.test.permissionusesjwt.dto.response.StudentAssignmentOverviewResponse;
import com.test.permissionusesjwt.dto.request.ApiResponse;
import com.test.permissionusesjwt.exception.AppException;
import com.test.permissionusesjwt.exception.ErrorCode;
import com.test.permissionusesjwt.security.AuthorizationService;
import com.test.permissionusesjwt.service.AssignmentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assignments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssignmentController {

    final AssignmentService assignmentService;
    AuthorizationService authorizationService;

    @PostMapping("/create-assignment")
    ApiResponse<AssignmentResponse> createAssignment(@RequestBody AssignmentRequest request) {
        
        // Kiểm tra quyền tạo bài tập
        if (!authorizationService.canCreateAssignment(request.getSectionId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        
        return ApiResponse.<AssignmentResponse>builder()
                .result(assignmentService.createAssignment(request))
                .build();
    }


    @PostMapping("/submit-with-answers")
    ApiResponse<CompletedAssignmentResponse> submitAssignmentWithAnswers(@RequestBody AssignmentSubmissionWithAnswersRequest request) {
        // Kiểm tra quyền nộp bài tập
        if (!authorizationService.canSubmitAssignment(request.getProgressId(), request.getAssignmentId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        return ApiResponse.<CompletedAssignmentResponse>builder()
                .result(assignmentService.submitAssignmentWithAnswers(request))
                .build();
    }


    @GetMapping("/completed/{completedAssignmentId}/detail")
    ApiResponse<CompletedAssignmentWithAnswersResponse> getCompletedAssignmentDetail(
            @PathVariable String completedAssignmentId) {
        
        // Kiểm tra quyền xem chi tiết bài tập
//        if (!authorizationService.canGradeAssignment(completedAssignmentId)) {
//            throw new AppException(ErrorCode.FORBIDDEN);
//        }
        
        return ApiResponse.<CompletedAssignmentWithAnswersResponse>builder()
                .result(assignmentService.getCompletedAssignmentDetail(completedAssignmentId))
                .build();
    }

    @PostMapping("/{assignmentId}/evaluate")
    ApiResponse<List<CompletedAssignmentResponse>> evaluateAssignment(
            @PathVariable String assignmentId,
            @RequestBody AssignmentEvaluationRequest request) {
        
        // Kiểm tra quyền đánh giá
        if (!authorizationService.canGradeAssignment(assignmentId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        
        return ApiResponse.<List<CompletedAssignmentResponse>>builder()
                .result(assignmentService.evaluateAssignment(assignmentId, request))
                .build();
    }

    @GetMapping("/{assignmentId}/submissions")
    ApiResponse<List<CompletedAssignmentWithAnswersResponse>> getAssignmentSubmissions(
            @PathVariable String assignmentId) {
        // Kiểm tra quyền xem bài tập đã nộp
        if (authorizationService.canGradeAssignment(assignmentId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        return ApiResponse.<List<CompletedAssignmentWithAnswersResponse>>builder()
                .result(assignmentService.getAssignmentSubmissions(assignmentId))
                .build();
    }

    @GetMapping("/course/{courseId}/student-submissions")
    ApiResponse<List<StudentAssignmentOverviewResponse>> getStudentAssignmentsByCourse(@PathVariable String courseId) {
        // Kiểm tra quyền truy cập (chỉ giảng viên)
        if (!authorizationService.canViewCourse(courseId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        
        return ApiResponse.<List<StudentAssignmentOverviewResponse>>builder()
                .result(assignmentService.getStudentAssignmentsByCourse(courseId))
                .build();
    }
} 