package com.test.permissionusesjwt.security;

import com.test.permissionusesjwt.authUtils.AuthUtils;
import com.test.permissionusesjwt.entity.*;

import com.test.permissionusesjwt.enums.ActiveStatus;
import com.test.permissionusesjwt.enums.InviteStatus;
import com.test.permissionusesjwt.exception.AppException;
import com.test.permissionusesjwt.exception.ErrorCode;
import com.test.permissionusesjwt.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("authorizationService")
@RequiredArgsConstructor
@Slf4j
public class AuthorizationService {

    private final AuthUtils authUtils;
    private final UserRepository userRepository;
    private final InstructorCourseRepository instructorCourseRepository;
    private final EnrollRepository enrollRepository;
    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ProgressRepository progressRepository;
    private final InstructorRepository instructorRepository;
    private final AssignmentRepository assignmentRepository;
    private final CompletedAssignmentRepository completedAssignmentRepository;


    public boolean canViewCourse(String courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXISTED));

        List<String> roles = authUtils.getRolesFromToken();
        String currentUsername = authUtils.getCurrentUsername();

        // 1. Nếu là Admin thì cho phép luôn
        if (roles.contains("ROLE_ADMIN")) {
            return true;
        }

        // 2. Lấy thông tin người dùng
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean hasAccess = false;

        // 3. Kiểm tra với tư cách INSTRUCTOR
        if (roles.contains("ROLE_INSTRUCTOR")) {
            Instructor instructor = userRepository.findInstructorByUsername(currentUsername).orElse(null);
            if (instructor != null) {
                boolean instructorCanView = instructorCourseRepository
                        .findByCourseIdAndInstructor(courseId, instructor)
                        .map(ic ->
                                ic.isOwner() ||
                                        (ic.getStatus() == InviteStatus.ACCEPTED &&
                                                ic.getPermissions().stream().anyMatch(p -> "QL".equalsIgnoreCase(p.getId())))
                        ).orElse(false);

                hasAccess |= instructorCanView; // gộp kết quả
            }
        }

        // 4. Kiểm tra với tư cách USER (học viên đã ghi danh chưa)
        if (roles.contains("ROLE_USER")) {
            boolean isEnrolled = enrollRepository.existsByProfileAndCourse(user.getProfile(), course);
            hasAccess |= isEnrolled;
        }

        return hasAccess;
    }


    public boolean canViewSection(String sectionId) {
        return sectionRepository.findById(sectionId)
                .map(section -> canViewCourse(section.getCourse().getId()))
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_EXISTED));
    }

    /**
     * Kiểm tra quyền xem tiến độ học tập
     * @param enrollmentId ID của enrollment
     * @return true nếu có quyền xem
     */
    public boolean canViewProgress(String enrollmentId) {
        // Lấy thông tin enrollment
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_EXISTED));

        List<String> roles = authUtils.getRolesFromToken();
        
        // Admin có thể xem tất cả
        if (roles.contains("ROLE_ADMIN")) {
            return true;
        }

        String currentUsername = authUtils.getCurrentUsername();
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Kiểm tra xem user hiện tại có phải là owner của enrollment không
        if (enrollment.getProfile().getUser().getUsername().equals(currentUsername)) {
            return true;
        }

        // Kiểm tra quyền instructor của khóa học
        if (roles.contains("ROLE_INSTRUCTOR")) {
            Instructor instructor = userRepository.findInstructorByUsername(currentUsername).orElse(null);
            if (instructor != null) {
                return instructorCourseRepository.findByCourseIdAndInstructor(
                        enrollment.getCourse().getId(), instructor)
                        .map(ic -> {
                            if (ic.isOwner()) {
                                return true;
                            }
                            return ic.getStatus() == InviteStatus.ACCEPTED
                                    && ic.getPermissions().stream().anyMatch(p -> "QL".equalsIgnoreCase(p.getId()));
                        })
                        .orElse(false);
            }
        }

        return false;
    }

    /**
     * Kiểm tra quyền xem tiến độ chi tiết
     * @param enrollmentId ID của enrollment
     * @return true nếu có quyền xem chi tiết
     */
    public boolean canViewDetailedProgress(String enrollmentId) {
        // Sử dụng cùng logic như canViewProgress
        return canViewProgress(enrollmentId);
    }

    /**
     * Kiểm tra quyền cập nhật tiến độ (hoàn thành bài học)
     * @param enrollmentId ID của enrollment
     * @return true nếu có quyền cập nhật
     */
    public boolean canUpdateProgress(String enrollmentId) {
        // Chỉ chủ sở hữu enrollment mới có thể cập nhật tiến độ của chính mình
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_EXISTED));

        String currentUsername = authUtils.getCurrentUsername();

        // Admin có thể cập nhật tất cả (nếu cần)
        List<String> roles = authUtils.getRolesFromToken();
        if (roles.contains("ROLE_ADMIN")) {
            return true;
        }

        // Chỉ chủ sở hữu enrollment mới có thể cập nhật
        return enrollment.getProfile().getUser().getUsername().equals(currentUsername);
    }

    /**
     * Kiểm tra quyền tạo bài tập
     * @param sectionId ID của section
     * @return true nếu có quyền tạo bài tập
     */
    public boolean canCreateAssignment(String sectionId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new AppException(ErrorCode.SECTION_NOT_FOUND));

        List<String> roles = authUtils.getRolesFromToken();
        log.info("User roles: {}", roles);

        // Admin có thể tạo bài tập
        if (roles.contains("ROLE_ADMIN")) {
            log.info("User is admin, returning false");
            return false;
        }

        // Chỉ instructor mới có thể tạo bài tập
        if (!roles.contains("ROLE_INSTRUCTOR")) {
            log.info("User is not instructor, returning true");
            return true;
        }

        String currentUsername = authUtils.getCurrentUsername();
        log.info("Current username: {}", currentUsername);

        Instructor instructor = userRepository.findInstructorByUsername(currentUsername).orElse(null);
        if (instructor == null) {
            log.info("Instructor not found for username: {}", currentUsername);
            return true;
        }

        log.info("Found instructor: {}", instructor.getId());
        log.info("Course ID: {}", section.getCourse().getId());

        // Kiểm tra quyền instructor của khóa học
        Optional<InstructorCourse> instructorCourseOpt = instructorCourseRepository.findByCourseIdAndInstructor(
                section.getCourse().getId(), instructor);

        if (instructorCourseOpt.isEmpty()) {
            log.warn("No InstructorCourse found for courseId: {} and instructor: {}",
                    section.getCourse().getId(), instructor.getId());
            return false;
        }

        InstructorCourse ic = instructorCourseOpt.get();
        log.info("Found InstructorCourse - isOwner: {}, status: {}, permissions: {}",
                ic.isOwner(), ic.getStatus(), ic.getPermissions().stream().map(Permission::getId).collect(Collectors.toList()));

        // Nếu là chủ sở hữu thì được tạo bài tập thoải mái
        if (ic.isOwner()) {
            log.info("Instructor is owner, returning true");
            return true;
        }

        // Nếu không phải chủ sở hữu, kiểm tra đã được chấp nhận và có quyền QL
        boolean hasAcceptedStatus = ic.getStatus() == InviteStatus.ACCEPTED;
        boolean hasQLPermission = ic.getPermissions().stream().anyMatch(p -> "QL".equalsIgnoreCase(p.getId()));

        log.info("Instructor is not owner - hasAcceptedStatus: {}, hasQLPermission: {}",
                hasAcceptedStatus, hasQLPermission);

        return hasAcceptedStatus && hasQLPermission;
    }

    /**
     * Kiểm tra quyền nộp bài tập
     * @param progressId ID của progress
     * @param assignmentId ID của bài tập
     * @return true nếu có quyền nộp bài tập
     */
    public boolean canSubmitAssignment(String progressId, String assignmentId) {
        try {
            // Lấy thông tin user hiện tại
            String currentUsername = authUtils.getCurrentUsername();

            // Kiểm tra progress tồn tại
            Progress progress = progressRepository.findById(progressId)
                    .orElseThrow(() -> new AppException(ErrorCode.PROGRESS_NOT_FOUND));

            // Lấy enrollment từ progress
            Enrollment enrollment = progress.getEnrollment();

            // Kiểm tra bài tập tồn tại
            Assignment assignment = assignmentRepository.findById(assignmentId)
                    .orElseThrow(() -> new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));

            // Kiểm tra xem user có phải là chủ sở hữu enrollment không
            boolean isEnrollmentOwner = enrollment.getProfile().getUser().getUsername().equals(currentUsername);
            if (!isEnrollmentOwner) {
                log.warn("User {} không phải là chủ sở hữu enrollment {}", currentUsername, enrollment.getId());
                return false;
            }

            // Kiểm tra xem enrollment có thuộc khóa học chứa bài tập này không
            boolean isAssignmentInEnrolledCourse = assignment.getSection().getCourse().getId()
                    .equals(enrollment.getCourse().getId());
            
            if (!isAssignmentInEnrolledCourse) {
                log.warn("Bài tập {} không thuộc khóa học của enrollment {}", assignmentId, enrollment.getId());
                return false;
            }

            // Kiểm tra xem đã nộp bài chưa
            CompletedAssignment existingSubmission = completedAssignmentRepository
                    .findByProgressIdAndAssignmentId(progressId, assignmentId)
                    .orElse(null);
            
            if (existingSubmission != null) {
                log.warn("User {} đã nộp bài tập {} rồi", currentUsername, assignmentId);
                return false;
            }

            return true;
        } catch (AppException e) {
            log.error("Lỗi khi kiểm tra quyền nộp bài tập: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Kiểm tra quyền xem bài tập đã hoàn thành
     * @param progressId ID của progress
     * @return true nếu có quyền xem
     */
    public boolean canViewCompletedAssignments(String progressId) {
        Progress progress = progressRepository.findById(progressId)
                .orElseThrow(() -> new AppException(ErrorCode.PROGRESS_NOT_FOUND));

        List<String> roles = authUtils.getRolesFromToken();
        
        // Admin có thể xem tất cả
        if (roles.contains("ROLE_ADMIN")) {
            return true;
        }

        String currentUsername = authUtils.getCurrentUsername();
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Chủ sở hữu progress có thể xem
        if (progress.getEnrollment().getProfile().getUser().getUsername().equals(currentUsername)) {
            return true;
        }

        // Instructor của khóa học có thể xem
        if (roles.contains("ROLE_INSTRUCTOR")) {
            Instructor instructor = userRepository.findInstructorByUsername(currentUsername).orElse(null);
            if (instructor != null) {
                return instructorCourseRepository.findByCourseIdAndInstructor(
                        progress.getEnrollment().getCourse().getId(), instructor)
                        .map(ic -> ic.isOwner() 
                                || ic.getStatus() == InviteStatus.ACCEPTED
                                || ic.getPermissions().stream().anyMatch(p -> "QL".equalsIgnoreCase(p.getId())))
                        .orElse(false);
            }
        }

        return false;
    }

    /**
     * Kiểm tra quyền xem câu trả lời của học viên
     * @param completedAssignmentId ID của bài tập đã hoàn thành
     * @return true nếu có quyền xem
     */
    public boolean canViewStudentAnswers(String completedAssignmentId) {
        try {
            CompletedAssignment completedAssignment = completedAssignmentRepository.findById(completedAssignmentId)
                    .orElseThrow(() -> new AppException(ErrorCode.COMPLETED_ASSIGNMENT_NOT_FOUND));

            List<String> roles = authUtils.getRolesFromToken();
            
            // Admin có thể xem tất cả
            if (roles.contains("ROLE_ADMIN")) {
                return true;
            }

            String currentUsername = authUtils.getCurrentUsername();
            User user = userRepository.findByUsername(currentUsername)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            // Chủ sở hữu bài tập đã hoàn thành có thể xem
            if (completedAssignment.getProgress().getEnrollment().getProfile().getUser().getUsername().equals(currentUsername)) {
                return true;
            }

            // Instructor của khóa học có thể xem
            if (roles.contains("ROLE_INSTRUCTOR")) {
                Instructor instructor = userRepository.findInstructorByUsername(currentUsername).orElse(null);
                if (instructor != null) {
                    return instructorCourseRepository.findByCourseIdAndInstructor(
                            completedAssignment.getProgress().getEnrollment().getCourse().getId(), instructor)
                            .map(ic -> ic.isOwner() 
                                    || ic.getStatus() == InviteStatus.ACCEPTED
                                    || ic.getPermissions().stream().anyMatch(p -> "QL".equalsIgnoreCase(p.getId())))
                            .orElse(false);
                }
            }

            return false;
        } catch (AppException e) {
            log.error("Lỗi khi kiểm tra quyền xem câu trả lời: {}", e.getMessage());
            return false;
        }
    }

    public boolean canGradeAssignment(String assignmentId) {
    try {
        List<String> roles = authUtils.getRolesFromToken();
        String currentUsername = authUtils.getCurrentUsername();

        // Admin có thể đánh giá tất cả
        if (roles.contains("ROLE_ADMIN")) {
            return true;
        }

        // Chỉ instructor mới có thể đánh giá
        if (roles.contains("ROLE_INSTRUCTOR")) {
            User user = userRepository.findByUsername(currentUsername)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            Instructor instructor = instructorRepository.findByUser(user)
                    .orElseThrow(() -> new AppException(ErrorCode.INSTRUCTOR_NOT_FOUND));

            // Tìm bài tập để lấy courseId
            Assignment assignment = assignmentRepository.findById(assignmentId)
                    .orElseThrow(() -> new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));

            String courseId = assignment.getSection().getCourse().getId();

            // Sử dụng method có JOIN FETCH để load permissions
            List<InstructorCourse> instructorCourses = instructorCourseRepository.findInstructorsWithPermissionsByCourseId(courseId);
            
            // Tìm instructor course của giảng viên hiện tại
            Optional<InstructorCourse> instructorCourseOpt = instructorCourses.stream()
                    .filter(ic -> ic.getInstructor().equals(instructor))
                    .findFirst();
            
            if (instructorCourseOpt.isEmpty()) {
                log.warn("Không tìm thấy InstructorCourse cho CourseId: {} và Instructor: {}", courseId, instructor.getId());
                return false;
            }
            
            InstructorCourse ic = instructorCourseOpt.get();
            log.info("Tìm thấy InstructorCourse - Owner: {}, Status: {}, Active: {}, Permissions: {}", 
                    ic.isOwner(), ic.getStatus(), ic.getIsActive(), 
                    ic.getPermissions().stream().map(p -> p.getId()).collect(Collectors.joining(",")));
            
            // Chủ khóa học có thể đánh giá tất cả
            if (ic.isOwner()) {
                return true;
            }
            
            // Giảng viên đồng giảng có trạng thái ACCEPTED, hoạt động và có quyền QL
            return ic.getStatus() == InviteStatus.ACCEPTED 
                    && ic.getIsActive() == ActiveStatus.Active
                    && ic.getPermissions().stream().anyMatch(p -> "QL".equalsIgnoreCase(p.getId()));
        }
        return false;
    } catch (Exception e) {
        log.error("Lỗi khi kiểm tra quyền đánh giá bài tập: {}", e.getMessage());
        return false;
    }
}
}
