package com.test.permissionusesjwt.service;

import com.test.permissionusesjwt.dto.request.ProgressCreateRequest;
import com.test.permissionusesjwt.dto.request.ProgressLessonRequest;
import com.test.permissionusesjwt.dto.response.*;
import com.test.permissionusesjwt.dto.response.AssignmentProgressResponse;
import com.test.permissionusesjwt.dto.response.StudentAnswerResponse;
import com.test.permissionusesjwt.entity.*;
import com.test.permissionusesjwt.entity.Assignment;
import com.test.permissionusesjwt.entity.CompletedAssignment;
import com.test.permissionusesjwt.entity.AnswerStudent;
import com.test.permissionusesjwt.enums.FinishStatus;
import com.test.permissionusesjwt.exception.AppException;
import com.test.permissionusesjwt.exception.ErrorCode;
import com.test.permissionusesjwt.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final ProgressRepository progressRepository;
    private final CompletedLessonRepository completedLessonRepository;
    private final LessonRepository lessonRepository;
    private final SectionRepository sectionRepository;
    private final AssignmentRepository assignmentRepository;
    private final CompletedAssignmentRepository completedAssignmentRepository;
    private final AnswerStudentRepository answerStudentRepository;


    @Transactional
    public ProgressResponse completeLesson(ProgressLessonRequest request) {
        // Kiểm tra progress tồn tại
        Progress progress = progressRepository.findByEnrollmentId(request.getEnrollmentId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        
        // Kiểm tra lesson tồn tại
        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_EXISTED));
        
        // Kiểm tra bài học đã hoàn thành chưa để tránh trùng lặp
        if (completedLessonRepository.existsByProgressIdAndLessonId(progress.getId(), lesson.getId())) {
            // Nếu đã hoàn thành rồi, trả về progress hiện tại
            return getProgressWithStats(progress);
        }
        
        // Tạo record hoàn thành bài học
        CompletedLesson completedLesson = CompletedLesson.builder()
                .progress(progress)
                .lesson(lesson)
                .finishedAt(LocalDateTime.now())
                .build();
        completedLessonRepository.save(completedLesson);

        // Tính toán lại tiến độ
        String courseId = progress.getEnrollment().getCourse().getId();
        String progressId = progress.getId();
        
        long totalLessons = lessonRepository.countLessonByCourseId(courseId);
        long completedLessons = completedLessonRepository.countByProgressId(progressId);
        long totalAssignments = assignmentRepository.countAssignmentByCourseId(courseId);
        long completedAssignments = completedAssignmentRepository.countByProgressId(progressId);
        
        // Kiểm tra hoàn thành khóa học (cả bài học và bài tập)
        int totalItems = (int) (totalLessons + totalAssignments);
        int completedItems = (int) (completedLessons + completedAssignments);
        
        if (totalItems > 0 && completedItems == totalItems) {
            progress.markCompleted();
            progressRepository.save(progress);
        }
        
        return getProgressWithStats(progress);
    }
    
    /**
     * Hoàn thành bài tập
     */
    @Transactional
    public ProgressResponse completeAssignment(String enrollmentId, String assignmentId) {
        // Kiểm tra progress tồn tại
        Progress progress = progressRepository.findByEnrollmentId(enrollmentId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        
        // Kiểm tra assignment tồn tại
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_EXISTED));
        
        // Kiểm tra bài tập đã hoàn thành chưa để tránh trùng lặp
        if (completedAssignmentRepository.findByProgressIdAndAssignmentId(progress.getId(), assignmentId).isPresent()) {
            // Nếu đã hoàn thành rồi, trả về progress hiện tại
            return getProgressWithStats(progress);
        }
        
        // Tạo record hoàn thành bài tập
        CompletedAssignment completedAssignment = CompletedAssignment.builder()
                .progress(progress)
                .assignment(assignment)
                .finishedAt(LocalDateTime.now())
                .status(FinishStatus.SUBMITTED)
                .build();
        completedAssignmentRepository.save(completedAssignment);

        // Tính toán lại tiến độ
        String courseId = progress.getEnrollment().getCourse().getId();
        String progressId = progress.getId();
        
        long totalLessons = lessonRepository.countLessonByCourseId(courseId);
        long completedLessons = completedLessonRepository.countByProgressId(progressId);
        long totalAssignments = assignmentRepository.countAssignmentByCourseId(courseId);
        long completedAssignments = completedAssignmentRepository.countByProgressId(progressId);
        
        // Kiểm tra hoàn thành khóa học (cả bài học và bài tập)
        int totalItems = (int) (totalLessons + totalAssignments);
        int completedItems = (int) (completedLessons + completedAssignments);
        
        if (totalItems > 0 && completedItems == totalItems) {
            progress.markCompleted();
            progressRepository.save(progress);
        }
        
        return getProgressWithStats(progress);
    }
    
    /**
     * Lấy thông tin tiến độ với thống kê
     */
    private ProgressResponse getProgressWithStats(Progress progress) {
        String courseId = progress.getEnrollment().getCourse().getId();
        String progressId = progress.getId();
        
        // Tính toán bài học
        long totalLessons = lessonRepository.countLessonByCourseId(courseId);
        long completedLessons = completedLessonRepository.countByProgressId(progressId);
        
        // Tính toán bài tập
        long totalAssignments = assignmentRepository.countAssignmentByCourseId(courseId);
        long completedAssignments = completedAssignmentRepository.countByProgressId(progressId);
        
        return ProgressResponse.from(progress, (int) completedLessons, (int) totalLessons, 
                                   (int) completedAssignments, (int) totalAssignments);
    }
    
    /**
     * Lấy tiến độ chi tiết của học viên
     */
    public ProgressDetailedResponse getDetailedProgress(String enrollmentId) {
        Progress progress = progressRepository.findByEnrollmentId(enrollmentId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
                
        Course course = progress.getEnrollment().getCourse();
        String courseId = course.getId();
        String progressId = progress.getId();
        
        // Lấy thống kê tổng thể
        long totalLessons = lessonRepository.countLessonByCourseId(courseId);
        long completedLessons = completedLessonRepository.countByProgressId(progressId);
        long totalAssignments = assignmentRepository.countAssignmentByCourseId(courseId);
        long completedAssignments = completedAssignmentRepository.countByProgressId(progressId);
        
        // Lấy thông tin sections
        List<SectionProgressResponse> sectionProgress = getSectionProgressList(progressId, courseId);
        
        // Lấy bài học tiếp theo cần học
        LessonProgressResponse nextLesson = getNextLessonToComplete(progressId);
        
        // Tính thống kê sections
        long totalSections = sectionProgress.size();
        long completedSections = sectionProgress.stream()
                .mapToLong(section -> section.getIsCompleted() ? 1 : 0)
                .sum();
        
        // Lấy thời gian bắt đầu (thời gian hoàn thành bài học đầu tiên)
        List<CompletedLesson> completedLessonsList = completedLessonRepository.findByProgressId(progressId);
        LocalDateTime startedAt = completedLessonsList.isEmpty() ? null : 
                completedLessonsList.stream()
                        .map(CompletedLesson::getFinishedAt)
                        .min(LocalDateTime::compareTo)
                        .orElse(null);
                        
        // Lấy hoạt động gần nhất
        LocalDateTime lastActivityAt = completedLessonsList.isEmpty() ? null :
                completedLessonsList.stream()
                        .map(CompletedLesson::getFinishedAt)
                        .max(LocalDateTime::compareTo)
                        .orElse(null);
        
        // Tính phần trăm tổng thể (bao gồm cả bài học và bài tập)
        double completionPercentage = 0.0;
        if (totalLessons > 0 || totalAssignments > 0) {
            int totalItems = (int) (totalLessons + totalAssignments);
            int completedItems = (int) (completedLessons + completedAssignments);
            completionPercentage = (double) completedItems / totalItems * 100;
        }
        
        return ProgressDetailedResponse.builder()
                .progressId(progress.getId())
                .courseId(courseId)
                .courseName(course.getName())
                .profileId(progress.getEnrollment().getProfile().getId())
                .finishStatus(progress.getFinishStatus().name())
                .finishedAt(progress.getFinishedAt())
                .completionPercentage(Math.round(completionPercentage * 100.0) / 100.0)
                .completedLessons((int) completedLessons)
                .totalLessons((int) totalLessons)
                .completedAssignments((int) completedAssignments)
                .totalAssignments((int) totalAssignments)
                .sectionProgress(sectionProgress)
                .nextLesson(nextLesson)
                .startedAt(startedAt)
                .lastActivityAt(lastActivityAt)
                .completedSections((int) completedSections)
                .totalSections((int) totalSections)
                .build();
    }
    
    /**
     * Lấy danh sách tiến độ theo section
     */
    public List<SectionProgressResponse> getSectionProgressList(String progressId, String courseId) {
        List<Section> sections = sectionRepository.findByCourseIdOrderByIndex(courseId);
        
        return sections.stream().map(section -> {
            // Tính toán bài học trong section
            long totalLessons = lessonRepository.countLessonBySectionId(section.getId());
            long completedLessons = completedLessonRepository.countCompletedLessonsByProgressAndSection(progressId, section.getId());
            
            // Tính toán bài tập trong section
            long totalAssignments = assignmentRepository.countAssignmentBySectionId(section.getId());
            long completedAssignments = completedAssignmentRepository.countByProgressIdAndSectionId(progressId, section.getId());
            
            // Tính phần trăm tổng thể (bao gồm cả bài học và bài tập)
            double completionPercentage = 0.0;
            if (totalLessons > 0 || totalAssignments > 0) {
                int totalItems = (int) (totalLessons + totalAssignments);
                int completedItems = (int) (completedLessons + completedAssignments);
                completionPercentage = (double) completedItems / totalItems * 100;
            }
            
            // Section được coi là hoàn thành khi tất cả bài học và bài tập đã hoàn thành
            boolean isCompleted = (totalLessons + totalAssignments) > 0 && 
                                 (completedLessons + completedAssignments) == (totalLessons + totalAssignments);
            
            // Lấy danh sách lessons trong section
            List<LessonProgressResponse> lessons = getLessonProgressList(progressId, section.getId());
            
            // Lấy danh sách assignments trong section
            List<AssignmentProgressResponse> assignments = getAssignmentProgressList(progressId, section.getId());
            
            return SectionProgressResponse.builder()
                    .sectionId(section.getId())
                    .sectionIndex(section.getIndex())
                    .completedLessons((int) completedLessons)
                    .totalLessons((int) totalLessons)
                    .completedAssignments((int) completedAssignments)
                    .totalAssignments((int) totalAssignments)
                    .completionPercentage(Math.round(completionPercentage * 100.0) / 100.0)
                    .isCompleted(isCompleted)
                    .lessons(lessons)
                    .assignments(assignments)
                    .build();
        }).collect(Collectors.toList());
    }
    
    /**
     * Lấy danh sách lesson progress trong một section
     */
    private List<LessonProgressResponse> getLessonProgressList(String progressId, String sectionId) {
        List<Lesson> lessons = lessonRepository.findLessonsBySectionIdOrderByIndex(sectionId);
        List<CompletedLesson> completedLessons = completedLessonRepository.findCompletedLessonsByProgressAndSection(progressId, sectionId);
        
        // Tạo map để tra cứu nhanh
        var completedLessonMap = completedLessons.stream()
                .collect(Collectors.toMap(
                        cl -> cl.getLesson().getId(),
                        CompletedLesson::getFinishedAt
                ));
        
        return lessons.stream().map(lesson -> {
            boolean isCompleted = completedLessonMap.containsKey(lesson.getId());
            LocalDateTime completedAt = completedLessonMap.get(lesson.getId());
            
            return LessonProgressResponse.builder()
                    .lessonId(lesson.getId())
                    .lessonIndex(lesson.getIndex())
                    .isCompleted(isCompleted)
                    .completedAt(completedAt)
                    .build();
        }).toList();
    }
    
    /**
     * Lấy danh sách assignment progress trong một section
     */
    private List<AssignmentProgressResponse> getAssignmentProgressList(String progressId, String sectionId) {
        List<Assignment> assignments = assignmentRepository.findAssignmentsBySectionIdOrderByIndex(sectionId);
        List<CompletedAssignment> completedAssignments = completedAssignmentRepository.findByProgressId(progressId)
                .stream()
                .filter(ca -> ca.getAssignment().getSection().getId().equals(sectionId))
                .toList();
        
        // Tạo map để tra cứu nhanh
        var completedAssignmentMap = completedAssignments.stream()
                .collect(Collectors.toMap(
                        ca -> ca.getAssignment().getId(),
                        ca -> ca
                ));
        
        return assignments.stream().map(assignment -> {
            CompletedAssignment completedAssignment = completedAssignmentMap.get(assignment.getId());
            boolean isCompleted = completedAssignment != null;
            LocalDateTime completedAt = isCompleted ? completedAssignment.getFinishedAt() : null;
            
            // Lấy câu trả lời của học viên nếu assignment đã hoàn thành
            List<StudentAnswerResponse> studentAnswers = null;
            if (isCompleted) {
                studentAnswers = getStudentAnswersForAssignment(completedAssignment.getId());
            }
            
            return AssignmentProgressResponse.builder()
                    .assignmentId(assignment.getId())
                    .assignmentIndex(assignment.getIndex())
                    .isCompleted(isCompleted)
                    .completedAt(completedAt)
                    .studentAnswers(studentAnswers)
                    .build();
        }).toList();
    }
    
    /**
     * Lấy câu trả lời của học viên cho một assignment
     */
    private List<StudentAnswerResponse> getStudentAnswersForAssignment(String completedAssignmentId) {
        List<AnswerStudent> answerStudents = answerStudentRepository.findByCompletedAssignmentId(completedAssignmentId);
        
        return answerStudents.stream().map(answerStudent -> {
            // Lấy đáp án chuẩn từ bảng dap_an_cau_hoi
            String correctAnswerContent = null;
            if (answerStudent.getQuestion().getAnswer() != null) {
                correctAnswerContent = answerStudent.getQuestion().getAnswer().getAnswerContent();
            }
            
            return StudentAnswerResponse.builder()
                    .questionId(answerStudent.getQuestion().getId())
                    .questionContent(answerStudent.getQuestion().getQuestionContent())
                    .answerContent(answerStudent.getAnswerContent())
                    .correctAnswerContent(correctAnswerContent)
                    .finishStatus(answerStudent.getFinishStatus().name())
                    .finishedTime(answerStudent.getFinishedTime())
                    .createdAt(answerStudent.getCreatedAt().toLocalDateTime())
                    .updatedAt(answerStudent.getUpdatedAt().toLocalDateTime())
                    .build();
        }).toList();
    }
    
    /**
     * Lấy bài học tiếp theo cần hoàn thành
     */
    private LessonProgressResponse getNextLessonToComplete(String progressId) {
        Lesson nextLesson = completedLessonRepository.findNextLessonToComplete(progressId);
        
        if (nextLesson == null) {
            return null;
        }
        
        return LessonProgressResponse.builder()
                .lessonId(nextLesson.getId())
                .isCompleted(false)
                .build();
    }
    
    /**
     * Lấy tiến độ đơn giản theo enrollmentId
     */
    public ProgressResponse getProgress(String enrollmentId) {
        Progress progress = progressRepository.findByEnrollmentId(enrollmentId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        
        return getProgressWithStats(progress);
    }
}
