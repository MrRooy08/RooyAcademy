package com.test.permissionusesjwt.service;

import com.test.permissionusesjwt.authUtils.AuthUtils;
import com.test.permissionusesjwt.dto.request.AssignmentRequest;
import com.test.permissionusesjwt.dto.request.AssignmentSubmissionWithAnswersRequest;
import com.test.permissionusesjwt.dto.request.AssignmentUpdateRequest;
import com.test.permissionusesjwt.dto.request.QuestionUpdateRequest;
import com.test.permissionusesjwt.dto.request.AssignmentEvaluationRequest;
import com.test.permissionusesjwt.dto.response.AssignmentResponse;
import com.test.permissionusesjwt.dto.response.CompletedAssignmentResponse;
import com.test.permissionusesjwt.dto.response.CompletedAssignmentWithAnswersResponse;
import com.test.permissionusesjwt.dto.response.StudentAnswerResponse;
import com.test.permissionusesjwt.dto.response.StudentAssignmentOverviewResponse;
import com.test.permissionusesjwt.dto.response.AssignmentSubmissionDetailResponse;
import com.test.permissionusesjwt.dto.response.CourseAssignmentSummaryResponse;
import com.test.permissionusesjwt.entity.*;
import com.test.permissionusesjwt.enums.FinishStatus;
import com.test.permissionusesjwt.exception.AppException;
import com.test.permissionusesjwt.exception.ErrorCode;
import com.test.permissionusesjwt.mapper.AssignmentMapper;
import com.test.permissionusesjwt.mapper.CompletedAssignmentMapper;
import com.test.permissionusesjwt.repository.*;
import com.test.permissionusesjwt.repository.InstructorRepository;
import com.test.permissionusesjwt.repository.AnswerStudentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AssignmentService {

    AssignmentMapper assignmentMapper;
    CompletedAssignmentMapper completedAssignmentMapper;
    AssignmentRepository assignmentRepository;
    SectionRepository sectionRepository;
    UserRepository userRepository;
    InstructorRepository instructorRepository;
    ProgressRepository progressRepository;
    CompletedAssignmentRepository completedAssignmentRepository;
    AssignmentQuestionRepository assignmentQuestionRepository;
    AssignmentAnswerRepository assignmentAnswerRepository;
    AnswerStudentRepository answerStudentRepository;
    EnrollmentRepository enrollmentRepository;
    AuthUtils authUtils;
    AssignmentQuestionService assignmentQuestionService;
    CourseRepository courseRepository;

    @Transactional
    public AssignmentResponse createAssignment(AssignmentRequest request) {
        // Kiểm tra quyền tạo bài tập (chỉ giảng viên)
        String currentUsername = authUtils.getCurrentUsername();
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Instructor instructor = instructorRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.INSTRUCTOR_NOT_FOUND));

        // Kiểm tra section tồn tại
        Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new AppException(ErrorCode.SECTION_NOT_FOUND));

        // Tạo bài tập
        Assignment assignment = Assignment.builder()
                .name(request.getName())
                .description(request.getDescription())
                .instructions(request.getInstructions())
                .estimatedTime(Integer.parseInt(request.getEstimatedTime()))
                .index(section.getAssignments().size() + 1)
                .section(section)
                .instructor(instructor)
                .build();

        assignment = assignmentRepository.save(assignment);

        // Tạo các câu hỏi và đáp án nếu có
        if (request.getQuestions() != null && !request.getQuestions().isEmpty()) {
            List<AssignmentQuestion> questionList = new ArrayList<>();

            for (var questionRequest : request.getQuestions()) {
                // Tạo câu hỏi
                AssignmentQuestion question = AssignmentQuestion.builder()
                        .questionContent(questionRequest.getQuestionContent())
                        .index(questionRequest.getIndex())
                        .assignment(assignment)
                        .build();

                question = assignmentQuestionRepository.save(question);

                // Tạo đáp án chuẩn
                Answer answer = Answer.builder()
                        .answerContent(questionRequest.getAnswerContent())
                        .question(question)
                        .build();

                answer = assignmentAnswerRepository.save(answer);

                // Đảm bảo relationship được thiết lập
                question.setAnswer(answer);
                question = assignmentQuestionRepository.save(question);

                questionList.add(question);
            }

            // Cập nhật assignment với danh sách câu hỏi
            assignment.setQuestions(questionList);
            assignment = assignmentRepository.save(assignment);
        }
        return assignmentMapper.toAssignmentResponse(assignment);
    }

    @Transactional
    public AssignmentResponse updateAssignment(String assignmentId, AssignmentUpdateRequest request) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        if (request.getName() != null) {
            assignment.setName(request.getName());
        }
        if (request.getDescription() != null) {
            assignment.setDescription(request.getDescription());
        }
        if (request.getInstructions() != null) {
            assignment.setInstructions(request.getInstructions());
        }
        if (request.getIndex() != null){
            assignment.setIndex(request.getIndex());
        }
        if (request.getEstimatedTime() != null) {
            assignment.setEstimatedTime(Integer.parseInt(request.getEstimatedTime()));
        }

        assignment = assignmentRepository.save(assignment);

        // Cập nhật câu hỏi và đáp án nếu có
        List<AssignmentQuestion> existingQuestions = assignmentQuestionRepository.findByAssignmentId(assignmentId);
        
        // Xử lý logic mới với questions chứa add, update, delete
        if (request.getQuestions() != null) {
            // Xử lý thêm câu hỏi mới (add)
            if (request.getQuestions().getAdd() != null && !request.getQuestions().getAdd().isEmpty()) {
                for (var questionRequest : request.getQuestions().getAdd()) {
                    // Tạo câu hỏi mới
                    AssignmentQuestion newQuestion = AssignmentQuestion.builder()
                            .questionContent(questionRequest.getQuestionContent())
                            .index(questionRequest.getIndex())
                            .assignment(assignment)
                            .build();

                    newQuestion = assignmentQuestionRepository.save(newQuestion);

                    // Tạo đáp án cho câu hỏi mới
                    Answer newAnswer = Answer.builder()
                            .answerContent(questionRequest.getAnswerContent())
                            .question(newQuestion)
                            .build();

                    newAnswer = assignmentAnswerRepository.save(newAnswer);
                    newQuestion.setAnswer(newAnswer);
                    assignmentQuestionRepository.save(newQuestion);
                }
            }
            
            // Xử lý cập nhật câu hỏi (update)
            if (request.getQuestions().getUpdate() != null && !request.getQuestions().getUpdate().isEmpty()) {
                for (var questionRequest : request.getQuestions().getUpdate()) {
                    if (questionRequest.getId() != null) {
                        // Tìm câu hỏi cần cập nhật
                        AssignmentQuestion existingQuestion = existingQuestions.stream()
                                .filter(q -> q.getId().equals(questionRequest.getId()))
                                .findFirst()
                                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));

                        // Cập nhật thông tin câu hỏi
                        existingQuestion.setQuestionContent(questionRequest.getQuestionContent());
                        existingQuestion.setIndex(questionRequest.getIndex());
                        existingQuestion = assignmentQuestionRepository.save(existingQuestion);

                        // Cập nhật đáp án
                        Answer existingAnswer = existingQuestion.getAnswer();
                        if (existingAnswer != null) {
                            existingAnswer.setAnswerContent(questionRequest.getAnswerContent());
                            assignmentAnswerRepository.save(existingAnswer);
                        } else {
                            // Tạo đáp án mới nếu chưa có
                            Answer newAnswer = Answer.builder()
                                    .answerContent(questionRequest.getAnswerContent())
                                    .question(existingQuestion)
                                    .build();
                            assignmentAnswerRepository.save(newAnswer);
                            existingQuestion.setAnswer(newAnswer);
                            assignmentQuestionRepository.save(existingQuestion);
                        }
                    }
                }
            }
            
            // Xử lý xóa câu hỏi (delete)
            if (request.getQuestions().getDelete() != null && !request.getQuestions().getDelete().isEmpty()) {
                for (String questionId : request.getQuestions().getDelete()) {
                    AssignmentQuestion questionToDelete = existingQuestions.stream()
                            .filter(q -> q.getId().equals(questionId))
                            .findFirst()
                            .orElse(null);
                    
                    if (questionToDelete != null) {
                        // Xóa đáp án trước
                        if (questionToDelete.getAnswer() != null) {
                            assignmentAnswerRepository.delete(questionToDelete.getAnswer());
                        }
                        // Xóa câu hỏi
                        assignmentQuestionRepository.delete(questionToDelete);
                    }
                }
            }
            
            // Flush để đảm bảo thay đổi được áp dụng ngay lập tức
            assignmentQuestionRepository.flush();
        }

        // Load lại assignment từ database để đảm bảo có đầy đủ dữ liệu
        assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        return assignmentMapper.toAssignmentResponse(assignment);
    }



    public AssignmentResponse getAssignmentById(String assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));
        
        AssignmentResponse response = assignmentMapper.toAssignmentResponse(assignment);
        
        // Lấy danh sách câu hỏi của bài tập
        boolean includeAnswer = false; // Không hiển thị đáp án cho học viên
//        response.setQuestions(assignmentQuestionService.getQuestionsByAssignment(assignmentId, includeAnswer));
        
        return response;
    }

    public List<AssignmentResponse> getAssignmentsBySection(String sectionId) {
        List<Assignment> assignments = assignmentRepository.findAssignmentsBySectionIdOrderByIndex(sectionId);
        return assignments.stream()
                .map(assignmentMapper::toAssignmentResponse)
                .collect(Collectors.toList());
    }

    public List<AssignmentResponse> getAssignmentsByCourse(String courseId) {
        List<Assignment> assignments = assignmentRepository.findAssignmentsByCourseIdOrderBySectionAndIndex(courseId);
        return assignments.stream()
                .map(assignmentMapper::toAssignmentResponse)
                .collect(Collectors.toList());
    }

//    @Transactional
//    public CompletedAssignmentResponse submitAssignment(AssignmentSubmissionRequest request) {
//        // Kiểm tra bài tập tồn tại
//        Assignment assignment = assignmentRepository.findById(request.getAssignmentId())
//                .orElseThrow(() -> new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));
//
//        // Kiểm tra progress tồn tại
//        Progress progress = progressRepository.findByEnrollmentId(request.getEnrollmentId())
//                .orElseThrow(() -> new AppException(ErrorCode.PROGRESS_NOT_FOUND));
//
//        // Kiểm tra xem đã nộp bài chưa
//        CompletedAssignment existingSubmission = completedAssignmentRepository
//                .findByProgressIdAndAssignmentId(progress.getId(), request.getAssignmentId())
//                .orElse(null);
//
//        if (existingSubmission != null) {
//            throw new AppException(ErrorCode.ASSIGNMENT_ALREADY_SUBMITTED);
//        }
//
//        // Tạo bài tập đã hoàn thành
//        CompletedAssignment completedAssignment = CompletedAssignment.builder()
//                .progress(progress)
//                .assignment(assignment)
//                .status("SUBMITTED")
//                .submission(request.getSubmission())
//                .build();
//
//        completedAssignment = completedAssignmentRepository.save(completedAssignment);
//
//        return completedAssignmentMapper.toCompletedAssignmentResponse(completedAssignment);
//    }
//
    @Transactional
    public CompletedAssignmentResponse submitAssignmentWithAnswers(AssignmentSubmissionWithAnswersRequest request) {
        // Kiểm tra bài tập tồn tại
        Assignment assignment = assignmentRepository.findById(request.getAssignmentId())
                .orElseThrow(() -> new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        // Kiểm tra progress tồn tại
        Progress progress = progressRepository.findById(request.getProgressId())
                .orElseThrow(() -> new AppException(ErrorCode.PROGRESS_NOT_FOUND));

        // Kiểm tra xem đã nộp bài chưa
        CompletedAssignment existingSubmission = completedAssignmentRepository
                .findByProgressIdAndAssignmentId(progress.getId(), request.getAssignmentId())
                .orElse(null);

        if (existingSubmission != null) {
            throw new AppException(ErrorCode.ASSIGNMENT_ALREADY_SUBMITTED);
        }

        // Tạo một CompletedAssignment duy nhất cho toàn bộ bài tập
        CompletedAssignment completedAssignment = CompletedAssignment.builder()
                .progress(progress)
                .assignment(assignment)
                .status(FinishStatus.SUBMITTED) // ✅ Sử dụng SUBMITTED thay vì FINISHED
                .build();

        completedAssignment = completedAssignmentRepository.save(completedAssignment);

        // Lấy tất cả câu hỏi của bài tập
        List<AssignmentQuestion> assignmentQuestions = assignmentQuestionRepository.findByAssignmentId(request.getAssignmentId());
        
        if (assignmentQuestions.isEmpty()) {
            throw new AppException(ErrorCode.ASSIGNMENT_HAS_NO_QUESTIONS);
        }

        // Kiểm tra xem học viên có trả lời đủ tất cả câu hỏi không
        if (request.getQuestions() == null || request.getQuestions().isEmpty()) {
            throw new AppException(ErrorCode.NO_ANSWERS_PROVIDED);
        }

        if (request.getQuestions().size() != assignmentQuestions.size()) {
            throw new AppException(ErrorCode.INCOMPLETE_ANSWERS);
        }

        // Lưu các câu trả lời của học viên cho từng câu hỏi
        for (var answerRequest : request.getQuestions()) {
            // Kiểm tra câu hỏi tồn tại và thuộc về bài tập này
            AssignmentQuestion question = assignmentQuestionRepository.findById(answerRequest.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));
            
            // Kiểm tra câu hỏi có thuộc về bài tập không
            if (!question.getAssignment().getId().equals(request.getAssignmentId())) {
                throw new AppException(ErrorCode.INVALID_QUESTION_FOR_ASSIGNMENT);
            }

            // Kiểm tra nội dung trả lời không được rỗng
            if (answerRequest.getAnswerContent() == null || answerRequest.getAnswerContent().trim().isEmpty()) {
                throw new AppException(ErrorCode.EMPTY_ANSWER_CONTENT);
            }

            // Tạo AnswerStudent cho câu trả lời với @EmbeddedId
            AnswerStudentId answerStudentId = new AnswerStudentId();
            answerStudentId.setCompletedAssignmentId(completedAssignment.getId());
            answerStudentId.setQuestionId(question.getId());

            AnswerStudent answerStudent = AnswerStudent.builder()
                    .id(answerStudentId) // ✅ Sử dụng @EmbeddedId
                    .answerContent(answerRequest.getAnswerContent().trim())
                    .finishStatus(FinishStatus.SUBMITTED) // ✅ Sử dụng SUBMITTED
                    .finishedTime(request.getFinishedTime())
                    .build();
            
            // Thiết lập relationship
            answerStudent.setCompletedAssignment(completedAssignment);
            answerStudent.setQuestion(question);
            
            // Lưu AnswerStudent
            answerStudentRepository.save(answerStudent);
        }

        return completedAssignmentMapper.toCompletedAssignmentResponse(completedAssignment);
    }

    public List<CompletedAssignmentResponse> getCompletedAssignmentsByProgress(String progressId) {
        List<CompletedAssignment> completedAssignments = completedAssignmentRepository.findByProgressId(progressId);

        return completedAssignments.stream()
                .map(completedAssignmentMapper::toCompletedAssignmentResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy danh sách bài tập đã hoàn thành với câu trả lời chi tiết
     */
    public List<CompletedAssignmentWithAnswersResponse> getCompletedAssignmentsWithAnswersByProgress(String progressId) {
        List<CompletedAssignment> completedAssignments = completedAssignmentRepository.findByProgressId(progressId);

        return completedAssignments.stream()
                .map(completedAssignment -> {
                    // Lấy câu trả lời của học viên
                    List<AnswerStudent> answerStudents = answerStudentRepository.findByCompletedAssignmentId(completedAssignment.getId());
                    
                    // Chuyển đổi thành StudentAnswerResponse
                    List<StudentAnswerResponse> studentAnswers = answerStudents.stream()
                            .map(answerStudent -> {
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
                            })
                            .collect(Collectors.toList());
                    
                    // Bổ sung thông tin đánh giá của giảng viên nếu đã được nhận xét
                    String instructorFeedback = null;
                    if (completedAssignment.getStatus() == FinishStatus.EVALUATED && completedAssignment.getFeedback() != null) {
                        instructorFeedback = completedAssignment.getFeedback();
                    }
                    
                    return CompletedAssignmentWithAnswersResponse.builder()
                            .id(completedAssignment.getId())
                            .assignmentId(completedAssignment.getAssignment().getId())
                            .assignmentName(completedAssignment.getAssignment().getName())
                            .progressId(completedAssignment.getProgress().getId())
                            .finishedAt(completedAssignment.getFinishedAt())
                            .feedback(instructorFeedback)
                            .status(completedAssignment.getStatus().name())
                            .studentAnswers(studentAnswers)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<CompletedAssignmentResponse> getCompletedAssignmentsByAssignment(String assignmentId) {
        List<CompletedAssignment> completedAssignments = completedAssignmentRepository.findByAssignmentId(assignmentId);
        
        return completedAssignments.stream()
                .map(completedAssignmentMapper::toCompletedAssignmentResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy chi tiết câu trả lời của học viên cho một bài tập đã hoàn thành
     */
    public List<AnswerStudent> getStudentAnswersByCompletedAssignment(String completedAssignmentId) {
        // Kiểm tra bài tập đã hoàn thành tồn tại
        CompletedAssignment completedAssignment = completedAssignmentRepository.findById(completedAssignmentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMPLETED_ASSIGNMENT_NOT_FOUND));
        
        // Lấy tất cả câu trả lời của học viên
        return answerStudentRepository.findById_CompletedAssignmentId(completedAssignmentId);
    }

    /**
     * Lấy câu trả lời cụ thể của học viên cho một câu hỏi
     */
    public AnswerStudent getStudentAnswerByQuestion(String completedAssignmentId, String questionId) {
        // Kiểm tra bài tập đã hoàn thành tồn tại
        CompletedAssignment completedAssignment = completedAssignmentRepository.findById(completedAssignmentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMPLETED_ASSIGNMENT_NOT_FOUND));
        
        // Lấy câu trả lời cụ thể
        AnswerStudent answerStudent = answerStudentRepository.findByCompletedAssignmentIdAndQuestionId(completedAssignmentId, questionId);
        
        if (answerStudent == null) {
            throw new AppException(ErrorCode.ANSWER_NOT_FOUND);
        }
        
        return answerStudent;
    }

    /**
     * Đánh giá câu trả lời của học viên (chỉ giảng viên)
     */
    @Transactional
    public AnswerStudent evaluateStudentAnswer(String completedAssignmentId, String questionId, String feedback) {
        // Kiểm tra quyền đánh giá (chỉ giảng viên)
        String currentUsername = authUtils.getCurrentUsername();
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Instructor instructor = instructorRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.INSTRUCTOR_NOT_FOUND));

        // Tìm câu trả lời của học viên
        AnswerStudent answerStudent = getStudentAnswerByQuestion(completedAssignmentId, questionId);
        
        // Kiểm tra xem người đánh giá có phải là giảng viên tạo bài tập không
        if (!answerStudent.getCompletedAssignment().getAssignment().getInstructor().equals(instructor)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        answerStudent.setFinishStatus(FinishStatus.EVALUATED);
        
        return answerStudentRepository.save(answerStudent);
    }

    /**
     * Đánh giá toàn bộ bài tập của học viên
     */
    @Transactional
    public CompletedAssignment evaluateAssignment(String completedAssignmentId, String overallFeedback) {
        // Kiểm tra quyền đánh giá (chỉ giảng viên)
        String currentUsername = authUtils.getCurrentUsername();
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Instructor instructor = instructorRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.INSTRUCTOR_NOT_FOUND));

        // Tìm bài tập đã hoàn thành
        CompletedAssignment completedAssignment = completedAssignmentRepository.findById(completedAssignmentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMPLETED_ASSIGNMENT_NOT_FOUND));

        // Kiểm tra xem người đánh giá có phải là giảng viên tạo bài tập không
        if (!completedAssignment.getAssignment().getInstructor().equals(instructor)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        // Cập nhật feedback và trạng thái bài tập
        completedAssignment.setFeedback(overallFeedback);
        completedAssignment.setStatus(FinishStatus.EVALUATED);
        
        return completedAssignmentRepository.save(completedAssignment);
    }

    /**
     * Đánh giá tất cả bài tập đã nộp của một bài tập cụ thể (API mới)
     */
    @Transactional
    public List<CompletedAssignmentResponse> evaluateAssignment(String assignmentId, AssignmentEvaluationRequest request) {
        List<CompletedAssignment> completedAssignments = completedAssignmentRepository.findByAssignmentId(assignmentId);
        List<CompletedAssignmentResponse> responses = new ArrayList<>();
        for (CompletedAssignment completedAssignment : completedAssignments) {
            completedAssignment.setFeedback(request.getFeedback());
            completedAssignment.setStatus(FinishStatus.EVALUATED);
            CompletedAssignment savedAssignment = completedAssignmentRepository.save(completedAssignment);
            responses.add(completedAssignmentMapper.toCompletedAssignmentResponse(savedAssignment));
        }
        return responses;
    }

    /**
     * Lấy danh sách bài tập đã hoàn thành của một bài tập cụ thể để đánh giá
     */
    public List<CompletedAssignmentWithAnswersResponse> getAssignmentSubmissions(String assignmentId) {
        // Kiểm tra quyền truy cập (chỉ giảng viên)
        String currentUsername = authUtils.getCurrentUsername();
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Instructor instructor = instructorRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.INSTRUCTOR_NOT_FOUND));

        // Tìm bài tập
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        // Kiểm tra xem người xem có phải là giảng viên tạo bài tập không
        if (!assignment.getInstructor().equals(instructor)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        // Lấy tất cả bài tập đã hoàn thành của bài tập này
        List<CompletedAssignment> completedAssignments = completedAssignmentRepository.findByAssignmentId(assignmentId);

        return completedAssignments.stream()
                .map(completedAssignment -> {
                    // Lấy tất cả câu trả lời của học viên
                    List<AnswerStudent> answerStudents = answerStudentRepository
                            .findByCompletedAssignmentId(completedAssignment.getId());

                    // Chuyển đổi thành StudentAnswerResponse
                    List<StudentAnswerResponse> studentAnswers = answerStudents.stream()
                            .map(answerStudent -> {
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
                            })
                            .collect(Collectors.toList());

                    Section section = assignment.getSection();

                    return CompletedAssignmentWithAnswersResponse.builder()
                            .id(completedAssignment.getId())
                            .assignmentId(assignment.getId())
                            .assignmentName(assignment.getName())
                            .assignmentDescription(assignment.getDescription())
                            .sectionName(section.getName())
                            .progressId(completedAssignment.getProgress().getId())
                            .finishedAt(completedAssignment.getFinishedAt())
                            .feedback(completedAssignment.getFeedback())
                            .status(completedAssignment.getStatus().name())
                            .studentAnswers(studentAnswers)
                            .createdAt(completedAssignment.getCreatedAt())
                            .updatedAt(completedAssignment.getUpdatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Lấy danh sách bài tập của học viên đã hoàn thành theo khoá học (dành cho giảng viên)
     */
    public List<StudentAssignmentOverviewResponse> getStudentAssignmentsByCourse(String courseId) {
        // Kiểm tra quyền truy cập (chỉ giảng viên)
        String currentUsername = authUtils.getCurrentUsername();
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Instructor instructor = instructorRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.INSTRUCTOR_NOT_FOUND));

        // Lấy tất cả enrollment của khoá học
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);
        
        return enrollments.stream()
                .map(enrollment -> {
                    // Lấy progress của học viên
                    Progress progress = progressRepository.findByEnrollmentId(enrollment.getId())
                            .orElse(null);
                    
                    if (progress == null) {
                        return null;
                    }

                    // Lấy tất cả bài tập đã hoàn thành của học viên
                    List<CompletedAssignment> allCompletedAssignments = completedAssignmentRepository
                            .findByProgressId(progress.getId());

                    // Lọc chỉ những bài tập thuộc khoá học này
                    List<CompletedAssignment> courseCompletedAssignments = allCompletedAssignments.stream()
                            .filter(ca -> ca.getAssignment().getSection().getCourse().getId().equals(courseId))
                            .toList();

                    // Chuyển đổi thành AssignmentSubmissionDetailResponse
                    List<AssignmentSubmissionDetailResponse> assignmentDetails = courseCompletedAssignments.stream()
                            .map(completedAssignment -> {
                                // Lấy câu trả lời của học viên
                                List<AnswerStudent> answerStudents = answerStudentRepository
                                        .findByCompletedAssignmentId(completedAssignment.getId());

                                List<StudentAnswerResponse> studentAnswers = answerStudents.stream()
                                        .map(answerStudent -> {
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
                                        })
                                        .collect(Collectors.toList());

                                Assignment assignment = completedAssignment.getAssignment();
                                Section section = assignment.getSection();

                                return AssignmentSubmissionDetailResponse.builder()
                                        .assignmentId(assignment.getId())
                                        .assignmentName(assignment.getName())
                                        .sectionName(section.getName())
                                        .assignmentIndex(assignment.getIndex())
                                        .sectionIndex(section.getIndex())
                                        .description(assignment.getDescription())
                                        .instructions(assignment.getInstructions())
                                        .estimatedTime(assignment.getEstimatedTime())
                                        .completedAssignmentId(completedAssignment.getId())
                                        .submittedAt(completedAssignment.getCreatedAt().toLocalDateTime())
                                        .finishedAt(completedAssignment.getFinishedAt())
                                        .status(completedAssignment.getStatus().name())
                                        .studentAnswers(studentAnswers)
                                        .overallFeedback(completedAssignment.getFeedback()) // Có thể thêm field này vào CompletedAssignment nếu cần
                                        .totalQuestions(answerStudents.size())
                                        .answeredQuestions((int) answerStudents.stream().filter(as -> as.getAnswerContent() != null && !as.getAnswerContent().isEmpty()).count())
                                        .build();
                            })
                            .collect(Collectors.toList());

                    // Tính toán thống kê
                    int totalAssignments = assignmentRepository.countByCourseId(courseId);
                    int completedAssignmentsCount = courseCompletedAssignments.size();
                    int evaluatedAssignmentsCount = (int) courseCompletedAssignments.stream()
                            .filter(ca -> ca.getStatus() == FinishStatus.EVALUATED)
                            .count();

                    return StudentAssignmentOverviewResponse.builder()
                            .courseId(courseId)
                            .courseName(enrollment.getCourse().getName())
                            .studentId(enrollment.getProfile().getUser().getId())
                            .studentName(enrollment.getProfile().getFirstName() + " " + enrollment.getProfile().getLastName())
                            .studentEmail(enrollment.getProfile().getUser().getUsername())
                            .enrollmentId(enrollment.getId())
                            .enrolledAt(enrollment.getEnrolled_at())
                            .assignments(assignmentDetails)
                            .totalAssignments(totalAssignments)
                            .completedAssignments(completedAssignmentsCount)
                            .evaluatedAssignments(evaluatedAssignmentsCount)
                            .build();
                })
                .filter(response -> response != null)
                .collect(Collectors.toList());
    }

    /**
     * Lấy tổng quan bài tập của một khoá học (dành cho giảng viên)
     */
//    public CourseAssignmentSummaryResponse getCourseAssignmentSummary(String courseId) {
//        // Kiểm tra quyền truy cập (chỉ giảng viên)
//        String currentUsername = authUtils.getCurrentUsername();
//        User user = userRepository.findByUsername(currentUsername)
//                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
//
//        Instructor instructor = instructorRepository.findByUser(user)
//                .orElseThrow(() -> new AppException(ErrorCode.INSTRUCTOR_NOT_FOUND));
//
//        // Lấy thông tin khoá học
//        Course course = courseRepository.findById(courseId)
//                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
//
//        // Lấy danh sách học viên và bài tập của họ
//        List<StudentAssignmentOverviewResponse> studentSubmissions = getStudentAssignmentsByCourse(courseId);
//
//        // Lấy tất cả bài tập của khoá học
//        List<Assignment> courseAssignments = assignmentRepository.findAssignmentsByCourseIdOrderBySectionAndIndex(courseId);
//
//        // Tạo summary cho từng bài tập
//        List<AssignmentSummaryResponse> assignmentSummaries = courseAssignments.stream()
//                .map(assignment -> {
//                    int totalSubmissions = (int) studentSubmissions.stream()
//                            .flatMap(ss -> ss.getAssignments().stream())
//                            .filter(ad -> ad.getAssignmentId().equals(assignment.getId()))
//                            .count();
//
//                    int evaluatedSubmissions = (int) studentSubmissions.stream()
//                            .flatMap(ss -> ss.getAssignments().stream())
//                            .filter(ad -> ad.getAssignmentId().equals(assignment.getId()) && "EVALUATED".equals(ad.getStatus()))
//                            .count();
//
//                    return AssignmentSummaryResponse.builder()
//                            .assignmentId(assignment.getId())
//                            .assignmentName(assignment.getName())
//                            .sectionName(assignment.getSection().getName())
//                            .assignmentIndex(assignment.getIndex())
//                            .sectionIndex(assignment.getSection().getIndex())
//                            .totalSubmissions(totalSubmissions)
//                            .evaluatedSubmissions(evaluatedSubmissions)
//                            .pendingEvaluations(totalSubmissions - evaluatedSubmissions)
//                            .build();
//                })
//                .collect(Collectors.toList());
//
//        return CourseAssignmentSummaryResponse.builder()
//                .courseId(courseId)
//                .courseName(course.getName())
//                .totalStudents(studentSubmissions.size())
//                .totalAssignments(courseAssignments.size())
//                .studentSubmissions(studentSubmissions)
//                .assignmentSummaries(assignmentSummaries)
//                .build();
//    }

    /**
     * Lấy danh sách bài tập của một học viên cụ thể trong khoá học
     */
    public StudentAssignmentOverviewResponse getStudentAssignmentsByCourseAndStudent(String courseId, String studentId) {
        // Kiểm tra quyền truy cập (chỉ giảng viên)
        String currentUsername = authUtils.getCurrentUsername();
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Instructor instructor = instructorRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.INSTRUCTOR_NOT_FOUND));

        // Tìm enrollment của học viên trong khoá học
        Enrollment enrollment = enrollmentRepository.findByCourseIdAndUserId(courseId, studentId)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

        // Lấy progress của học viên
        Progress progress = progressRepository.findByEnrollmentId(enrollment.getId())
                .orElseThrow(() -> new AppException(ErrorCode.PROGRESS_NOT_FOUND));

        // Lấy tất cả bài tập đã hoàn thành của học viên trong khoá học
        List<CompletedAssignment> completedAssignments = completedAssignmentRepository
                .findByProgressId(progress.getId())
                .stream()
                .filter(ca -> ca.getAssignment().getSection().getCourse().getId().equals(courseId))
                .collect(Collectors.toList());

        // Chuyển đổi thành AssignmentSubmissionDetailResponse
        List<AssignmentSubmissionDetailResponse> assignmentDetails = completedAssignments.stream()
                .map(completedAssignment -> {
                    // Lấy câu trả lời của học viên
                    List<AnswerStudent> answerStudents = answerStudentRepository
                            .findByCompletedAssignmentId(completedAssignment.getId());

                    List<StudentAnswerResponse> studentAnswers = answerStudents.stream()
                            .map(answerStudent -> {
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
                            })
                            .collect(Collectors.toList());

                    Assignment assignment = completedAssignment.getAssignment();
                    Section section = assignment.getSection();

                    return AssignmentSubmissionDetailResponse.builder()
                            .assignmentId(assignment.getId())
                            .assignmentName(assignment.getName())
                            .sectionName(section.getName())
                            .assignmentIndex(assignment.getIndex())
                            .sectionIndex(section.getIndex())
                            .description(assignment.getDescription())
                            .instructions(assignment.getInstructions())
                            .estimatedTime(assignment.getEstimatedTime())
                            .completedAssignmentId(completedAssignment.getId())
                            .submittedAt(completedAssignment.getCreatedAt().toLocalDateTime())
                            .finishedAt(completedAssignment.getFinishedAt())
                            .status(completedAssignment.getStatus().name())
                            .studentAnswers(studentAnswers)
                            .overallFeedback(completedAssignment.getFeedback())
                            .totalQuestions(answerStudents.size())
                            .answeredQuestions((int) answerStudents.stream().filter(as -> as.getAnswerContent() != null && !as.getAnswerContent().isEmpty()).count())
                            .build();
                })
                .collect(Collectors.toList());

        // Tính toán thống kê
        int totalAssignments = assignmentRepository.countByCourseId(courseId);
        int completedAssignmentsCount = completedAssignments.size();
        int evaluatedAssignmentsCount = (int) completedAssignments.stream()
                .filter(ca -> ca.getStatus() == FinishStatus.EVALUATED)
                .count();

        return StudentAssignmentOverviewResponse.builder()
                .courseId(courseId)
                .courseName(enrollment.getCourse().getName())
                .studentId(enrollment.getProfile().getUser().getId())
                .studentName(enrollment.getProfile().getFirstName() + " " + enrollment.getProfile().getLastName())
                .studentEmail(enrollment.getProfile().getUser().getUsername())
                .enrollmentId(enrollment.getId())
                .enrolledAt(enrollment.getEnrolled_at())
                .assignments(assignmentDetails)
                .totalAssignments(totalAssignments)
                .completedAssignments(completedAssignmentsCount)
                .evaluatedAssignments(evaluatedAssignmentsCount)
                .build();
    }

//    @Transactional
//    public CompletedAssignmentResponse evaluateAssignment(String progressId, String assignmentId, String feedback) {
//        // Kiểm tra quyền đánh giá (chỉ giảng viên)
//        String currentUsername = authUtils.getCurrentUsername();
//        User user = userRepository.findByUsername(currentUsername)
//                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
//
//        Instructor instructor = instructorRepository.findByUser(user)
//                .orElseThrow(() -> new AppException(ErrorCode.INSTRUCTOR_NOT_FOUND));
//
//        // Tìm bài tập đã nộp
//        CompletedAssignment completedAssignment = completedAssignmentRepository
//                .findByProgressIdAndAssignmentId(progressId, assignmentId)
//                .orElseThrow(() -> new AppException(ErrorCode.COMPLETED_ASSIGNMENT_NOT_FOUND));
//
//        // Kiểm tra xem người đánh giá có phải là giảng viên tạo bài tập không
//        if (!completedAssignment.getAssignment().getInstructor().equals(instructor)) {
//            throw new AppException(ErrorCode.FORBIDDEN);
//        }
//
//        // Cập nhật nhận xét
//        completedAssignment.setFeedback(feedback);
//        completedAssignment.setStatus("EVALUATED");
//
//        completedAssignment = completedAssignmentRepository.save(completedAssignment);
//
//        return completedAssignmentMapper.toCompletedAssignmentResponse(completedAssignment);
//    }

    /**
     * Lấy thông tin chi tiết bài tập đã hoàn thành để đánh giá
     */
    public CompletedAssignmentWithAnswersResponse getCompletedAssignmentDetail(String completedAssignmentId) {
        // Kiểm tra quyền truy cập (chỉ giảng viên)
        String currentUsername = authUtils.getCurrentUsername();
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Instructor instructor = instructorRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.INSTRUCTOR_NOT_FOUND));

        // Tìm bài tập đã hoàn thành
        CompletedAssignment completedAssignment = completedAssignmentRepository.findById(completedAssignmentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMPLETED_ASSIGNMENT_NOT_FOUND));

        // Kiểm tra xem người xem có phải là giảng viên tạo bài tập không
        if (!completedAssignment.getAssignment().getInstructor().equals(instructor)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        // Lấy tất cả câu trả lời của học viên
        List<AnswerStudent> answerStudents = answerStudentRepository
                .findByCompletedAssignmentId(completedAssignmentId);

        // Chuyển đổi thành StudentAnswerResponse
        List<StudentAnswerResponse> studentAnswers = answerStudents.stream()
                .map(answerStudent -> {
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
                })
                .collect(Collectors.toList());

        Assignment assignment = completedAssignment.getAssignment();
        Section section = assignment.getSection();

        return CompletedAssignmentWithAnswersResponse.builder()
                .id(completedAssignment.getId())
                .assignmentId(assignment.getId())
                .assignmentName(assignment.getName())
                .assignmentDescription(assignment.getDescription())
                .sectionName(section.getName())
                .progressId(completedAssignment.getProgress().getId())
                .finishedAt(completedAssignment.getFinishedAt())
                .feedback(completedAssignment.getFeedback())
                .status(completedAssignment.getStatus().name())
                .studentAnswers(studentAnswers)
                .createdAt(completedAssignment.getCreatedAt())
                .updatedAt(completedAssignment.getUpdatedAt())
                .build();
    }
} 