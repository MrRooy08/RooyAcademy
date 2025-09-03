package com.test.permissionusesjwt.controller;

import com.test.permissionusesjwt.dto.request.ProgressCreateRequest;
import com.test.permissionusesjwt.dto.request.ProgressLessonRequest;
import com.test.permissionusesjwt.dto.request.ApiResponse;
import com.test.permissionusesjwt.dto.response.*;
import com.test.permissionusesjwt.dto.response.CompletedAssignmentWithAnswersResponse;
import com.test.permissionusesjwt.service.AssignmentService;
import com.test.permissionusesjwt.exception.AppException;
import com.test.permissionusesjwt.exception.ErrorCode;
import com.test.permissionusesjwt.security.AuthorizationService;
import com.test.permissionusesjwt.service.ProgressService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/progress")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProgressController {

    ProgressService progressService;
    AuthorizationService authorizationService;
    AssignmentService assignmentService;

    @PostMapping("/complete-lesson")
    ApiResponse<ProgressResponse> completeLesson(@RequestBody ProgressLessonRequest request) {
        // Kiểm tra quyền cập nhật tiến độ
        if (!authorizationService.canUpdateProgress(request.getEnrollmentId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        
        return ApiResponse.<ProgressResponse>builder()
                .result(progressService.completeLesson(request))
                .build();
    }
    
    /**
     * Hoàn thành bài tập
     */
    @PostMapping("/complete-assignment")
    ApiResponse<ProgressResponse> completeAssignment(@RequestParam String enrollmentId, @RequestParam String assignmentId) {
        // Kiểm tra quyền cập nhật tiến độ
        if (!authorizationService.canUpdateProgress(enrollmentId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        
        return ApiResponse.<ProgressResponse>builder()
                .result(progressService.completeAssignment(enrollmentId, assignmentId))
                .build();
    }
    
    /**
     * Lấy tiến độ đơn giản theo enrollmentId
     */
    @GetMapping("/{enrollmentId}")
    ApiResponse<ProgressResponse> getProgress(@PathVariable String enrollmentId) {
        // Kiểm tra quyền xem tiến độ
        if (!authorizationService.canViewProgress(enrollmentId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        
        return ApiResponse.<ProgressResponse>builder()
                .result(progressService.getProgress(enrollmentId))
                .build();
    }
    
    /**
     * Lấy tiến độ chi tiết với thông tin section và lesson
     * Chỉ có học viên đã đăng ký, admin, hoặc giảng viên của khóa học mới có thể xem
     */
    @GetMapping("/{enrollmentId}/detailed")
    ApiResponse<ProgressDetailedResponse> getDetailedProgress(@PathVariable String enrollmentId) {
        // Kiểm tra quyền xem tiến độ chi tiết
        if (!authorizationService.canViewDetailedProgress(enrollmentId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        
        return ApiResponse.<ProgressDetailedResponse>builder()
                .result(progressService.getDetailedProgress(enrollmentId))
                .build();
    }

    /**
     * Lấy danh sách bài tập đã hoàn thành theo enrollment
     */
    @GetMapping("/{enrollmentId}/assignments")
    ApiResponse<List<CompletedAssignmentWithAnswersResponse>> getCompletedAssignments(@PathVariable String enrollmentId) {
        // Kiểm tra quyền xem tiến độ
        if (!authorizationService.canViewProgress(enrollmentId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        
        // Lấy progress để có progressId
        ProgressResponse progress = progressService.getProgress(enrollmentId);
        return ApiResponse.<List<CompletedAssignmentWithAnswersResponse>>builder()
                .result(assignmentService.getCompletedAssignmentsWithAnswersByProgress(progress.getId()))
                .build();
    }
}
