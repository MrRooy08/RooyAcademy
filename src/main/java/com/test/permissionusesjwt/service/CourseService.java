package com.test.permissionusesjwt.service;


import com.test.permissionusesjwt.authUtils.AuthUtils;
import com.test.permissionusesjwt.dto.request.*;
import com.test.permissionusesjwt.dto.request.CoursePriceUpdateRequest;
import com.test.permissionusesjwt.dto.response.*;
import com.test.permissionusesjwt.entity.*;
import com.test.permissionusesjwt.enums.ActiveStatus;
import com.test.permissionusesjwt.enums.ApproveStatus;
import com.test.permissionusesjwt.enums.CourseStatus;
import com.test.permissionusesjwt.enums.InviteStatus;
import com.test.permissionusesjwt.exception.AppException;
import com.test.permissionusesjwt.exception.ErrorCode;
import com.test.permissionusesjwt.mapper.CourseMapper;
import com.test.permissionusesjwt.mapper.InstructorCourseMapper;
import com.test.permissionusesjwt.mapper.SectionMapper;
import com.test.permissionusesjwt.mapper.LessonMapper;
import com.test.permissionusesjwt.repository.*;
import com.test.permissionusesjwt.security.AuthorizationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import com.test.permissionusesjwt.entity.Section;
import com.test.permissionusesjwt.entity.Lesson;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CourseService {
    private final InstructorCourseMapper instructorCourseMapper;
    CourseRepository courseRepository;
    CourseMapper courseMapper;
    LevelRepository levelRepository;
    EmailService emailService;
    private final CategoryRepository categoryRepository;
    private final InstructorRepository instructorRepository;
    private final UserRepository userRepository;
    private final InstructorCourseRepository instructorCourseRepository;
    private final AuthUtils authUtils;
    private final TierPriceRepository tierPriceRepository;
    private final SectionMapper sectionMapper;
    private final LessonMapper lessonMapper;
    private final AuthorizationService authorizationService;
    private final PermissionRepository permissionRepository;
    private final FileStorageService fileStorageService;
    private final CourseMetaRepository courseMetaRepository;
    private final SectionRepository sectionRepository;
    private final TopicRepository topicRepository;
    private final EnrollRepository enrollRepository;
    AssignmentService assignmentService;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> approvePublicCourse(String courseId) {
        String username = authUtils.getCurrentUsername();
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXISTED));
        course.setApproveStatus(ApproveStatus.APPROVED);
        course.setIsActive(CourseStatus.PUBLIC);
        course.setUser(user);
        course.setApprovedAt(LocalDateTime.now());
        course = courseRepository.save(course);
        return ResponseEntity.ok().build();
    }



    @Transactional
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<Void> submitPublicCourse(String courseId) {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXISTED));

            String username = authUtils.getCurrentUsername();

            Instructor instructor = userRepository.findInstructorByUsername(username).orElseThrow(
                    () -> new AppException(ErrorCode.USER_NOT_EXISTED)
            );

            instructorCourseRepository.findByCourseIdAndInstructor(courseId, instructor).orElseThrow(
                    () -> new AppException(ErrorCode.COURSE_NOT_EXISTED)
            );

            List<CourseMeta> courseMeta = courseMetaRepository.findAllByCourseId(courseId).orElseThrow(
                    () -> new AppException(ErrorCode.COURSE_NOT_EXISTED)
            );

            List<Section> section = sectionRepository.findByCourseId(courseId);
            if (course.getCategory() == null || course.getLevelCourse() == null
                || course.getImageUrl() == null || course.getVideoUrl() == null
                    || course.getTopic() == null
            ) {
                throw new AppException(ErrorCode.COURSE_NOT_EXISTED);
            }
            if (section.size() < 0 && courseMeta.isEmpty()) {
                throw new AppException(ErrorCode.COURSE_NOT_EXISTED);
            }
            course.setApproveStatus(ApproveStatus.PROCESSING);
            course = courseRepository.save(course);
        return ResponseEntity.ok().build();
    }



    @PreAuthorize("hasRole('INSTRUCTOR')")
    public CourseResponse createDraftCourse (CourseDraftRequest courseRequest){
        Category categoryId = categoryRepository.findByName(courseRequest.getCategory()).orElseThrow(
                () -> new AppException(ErrorCode.LEVEL_NOT_EXISTED)
        );
        courseRequest.setCategory(categoryId.getId());

//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        Jwt jwt = (Jwt) auth.getPrincipal();
//        String username = jwt.getSubject();

        String username = authUtils.getCurrentUsername();

        Instructor instructor = userRepository.findInstructorByUsername(username).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        Course course = courseMapper.toCourse(courseRequest);
        course.setIsActive(CourseStatus.DRAFT);
        Set<Permission> permissions = new HashSet<>();
        permissions = new HashSet<>(permissionRepository.findAll());
        InstructorCourse instructorCourse = InstructorCourse.builder()
                .instructor(instructor)
                .course(course)
                .isOwner(true)
                .isActive(ActiveStatus.Active)
                .createdAt(LocalDateTime.now())
                .permissions(permissions)
                .build();

        course.getInstructorCourse().add(instructorCourse);
        courseRepository.save(course);
        return courseMapper.toCourseResponse(course);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Transactional
    public ResponseEntity<String> updateCoursePrice(String courseId, CoursePriceUpdateRequest request) {
        String username = authUtils.getCurrentUsername();
        Instructor instructor = userRepository.findInstructorByUsername(username).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXISTED));
        boolean isOwner = instructorCourseRepository.existsByCourse_IdAndInstructorAndIsOwnerTrue(courseId, instructor);
        if (!isOwner) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        TierPrice tierPrice = tierPriceRepository.findById(request.getId()).orElseThrow(
                () -> new AppException(ErrorCode.TIER_PRICE_NOT_EXISTED)
        );
        course.setPrice(tierPrice);
        course = courseRepository.save(course);
        return ResponseEntity.ok("Cập nhật giá khóa học thành công");
    }

    public String getCoursePriceId(String courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXISTED));
        TierPrice tierPrice = course.getPrice();
        if (tierPrice == null) {
            throw new AppException(ErrorCode.TIER_PRICE_NOT_EXISTED);
        }
        return tierPrice.getId();
    }

    public HandleInstructorDto addCoInstructor (String courseId , HandleInstructorDto request)
    {
        String username = authUtils.getCurrentUsername();
        Instructor instructor = userRepository.findInstructorByUsername(username).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXISTED));

        boolean isOwner = instructorCourseRepository.existsByCourse_IdAndInstructorAndIsOwnerTrue(courseId, instructor);
        if (!isOwner) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // ----- Handle added instructors -----
        if (request.getAdded() != null && !request.getAdded().isEmpty()) {
            // Xử lý giảng viên được thêm
            for (AddInstructorRequest insRequest : request.getAdded()) {
                Instructor invitedInstructor = userRepository.findInstructorByUsername(insRequest.getInstructor())
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

                // Nếu đã tồn tại quan hệ -> lỗi
                boolean isInvitingOwner = instructorCourseRepository.existsByCourse_IdAndInstructorAndIsOwnerTrue(courseId, invitedInstructor);

                if (isInvitingOwner) {
                    throw new AppException(ErrorCode.CANNOT_INVITE_COURSE_OWNER);
                }

                InstructorCourse newIC = InstructorCourse.builder()
                        .course(course)
                        .instructor(invitedInstructor)
                        .isOwner(false)
                        .status(InviteStatus.PENDING)
                        .isActive(ActiveStatus.Inactive)
                        .pendingPermissionIds(insRequest.getPermissions())
                        .build();
                instructorCourseRepository.save(newIC);
                emailService.sendDashboardLinkEmail(insRequest.getInstructor(), "Bạn có lời moời làm giảng viên ne");
            }
        }

        // ----- Handle removed instructors -----
        if (request.getRemoved() != null && !request.getRemoved().isEmpty()) {
            for (AddInstructorRequest rmRequest : request.getRemoved()) {
                Instructor removeInstructor = instructorRepository.findById(rmRequest.getInstructor())
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

                InstructorCourse ic = instructorCourseRepository.findByCourseIdAndInstructor(courseId, removeInstructor)
                        .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXISTED));

                ic.setIsActive(ActiveStatus.Inactive);
                ic.getPermissions().clear();
                ic.getPendingPermissionIds().clear();
                instructorCourseRepository.save(ic);
            }
        }

        // ----- Handle permission changes -----
        if (request.getPermissionChanged() != null && !request.getPermissionChanged().isEmpty()) {
            for (AddInstructorRequest pcRequest : request.getPermissionChanged()) {
                Instructor targetInstructor = instructorRepository.findById(pcRequest.getInstructor())
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

                InstructorCourse ic = instructorCourseRepository.findByCourseIdAndInstructor(courseId, targetInstructor)
                        .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXISTED));

                if (ic.getIsActive() != ActiveStatus.Active) {
                    if (pcRequest.getPermissions() == null || pcRequest.getPermissions().isEmpty()) {
                        ic.getPendingPermissionIds().clear();
                    } else {
                        ic.setPendingPermissionIds(new HashSet<>(pcRequest.getPermissions()));
                    }
                } 
                else {
                    if (pcRequest.getPermissions() == null || pcRequest.getPermissions().isEmpty()) {
                        ic.getPermissions().clear();
                    } else {
                        Set<Permission> newPerms = new HashSet<>(permissionRepository.findAllById(pcRequest.getPermissions()));
                        ic.setPermissions(newPerms);
                    }
                }
                instructorCourseRepository.save(ic);
            }
        }

        // Trả về payload gốc để FE tự hiển thị
        return request;
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    public List<AddInstructorResponse> getMyInvitations() {
        String username = authUtils.getCurrentUsername();
        Instructor instructor = userRepository.findInstructorByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<InstructorCourse> invitations = instructorCourseRepository
                .findByInstructor_IdAndStatus(instructor.getId(), InviteStatus.PENDING);

        return invitations.stream()
                .map(instructorCourse -> {
                    String courseId = instructorCourse.getCourse().getId();
                    String instructorId = instructorCourse.getInstructor().getId();
                    String status = "PENDING";
                    Set<String> pendingPermissionIds = instructorCourse.getPendingPermissionIds();

                    // Tính toán thời gian đã trôi qua bằng phương thức đã tối ưu
                    String time = calculateTimeElapsed(instructorCourse.getInvitedAt());

                    return AddInstructorResponse.builder()
                            .courseId(courseId)
                            .instructorId(instructorId)
                            .status(status)
                            .permissions(pendingPermissionIds)
                            .time(time)
                            .build();
                })
                .collect(Collectors.toList());
    }


    private String calculateTimeElapsed(LocalDateTime invitedAt) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(invitedAt, now);

        long seconds = duration.getSeconds();  // Tổng số giây đã trôi qua

        if (seconds < 60) {
            return seconds + " seconds";  // Nếu dưới 60 giây, trả về giây
        }

        long minutes = seconds / 60;  // Chuyển đổi sang phút
        if (minutes < 60) {
            return minutes + " minutes";  // Nếu dưới 60 phút, trả về phút
        }

        long hours = minutes / 60;  // Chuyển đổi sang giờ
        if (hours < 24) {
            return hours + " hours";  // Nếu dưới 24 giờ, trả về giờ
        }

        long days = hours / 24;  // Chuyển đổi sang ngày
        return days + " days";  // Nếu trên 24 giờ, trả về ngày
    }


    @PreAuthorize("hasRole('INSTRUCTOR')")
    public InvitationResponse respondToCoInstructorInvite(String courseId, boolean accept) {
        String username = authUtils.getCurrentUsername();
        Instructor instructor = userRepository.findInstructorByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        InstructorCourse instructorCourse = instructorCourseRepository
                .findByCourseIdAndInstructor(courseId, instructor)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXISTED));

        if (instructorCourse.getStatus() != InviteStatus.PENDING) {
            throw new AppException(ErrorCode.LEVEL_NOT_EXISTED);
        }
        String responseMessage = "";
        if (accept) {
            // Move pending permissions to official permissions
            Set<String> pendingIds = instructorCourse.getPendingPermissionIds();
            if (pendingIds != null && !pendingIds.isEmpty()) {
                List<Permission> newPermissions = permissionRepository.findAllById(pendingIds);
                instructorCourse.getPermissions().addAll(newPermissions);
                instructorCourse.getPendingPermissionIds().clear();
            }

            instructorCourse.setStatus(InviteStatus.ACCEPTED);
            instructorCourse.setIsActive(ActiveStatus.Active);
            instructorCourse.setCreatedAt(LocalDateTime.now());
            instructorCourseRepository.save(instructorCourse);
            responseMessage = "Your invitation is being accepted";
        } else {
            instructorCourse.setStatus(InviteStatus.REJECTED);
            responseMessage = "Your invitation is being rejected";
            instructorCourseRepository.delete(instructorCourse);
        }

        return InvitationResponse.builder()
                .courseId(courseId)
                .status(String.valueOf(instructorCourse.getStatus()))
                .message(responseMessage)
                .build();
    }



    @PreAuthorize("hasRole('INSTRUCTOR')")
    public CourseResponse createCourse(CourseRequest courseRequest) {

//       Level level = levelRepository.findLevelByName(courseRequest.getLevelCourse()).orElseThrow(
//                ()-> new AppException(ErrorCode.LEVEL_NOT_EXISTED)
//       );
//      course.setLevelCourse(level);

        Category category = categoryRepository.findByName(courseRequest.getCategory()).orElseThrow(
                ()-> new AppException(ErrorCode.LEVEL_NOT_EXISTED)
        );
        Course course = courseMapper.toCourse(courseRequest);
        course.setCategory(category);
        course.setIsActive(CourseStatus.DRAFT);
        course.setRangeCourse(CourseStatus.PUBLIC);

        LocalDateTime now = LocalDateTime.now();
        course.setCreatedAt(now);
        course.setUpdatedAt(now);
        try{
            course = courseRepository.save(course);

        }
        catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.COURSE_EXISTED);
        }

        return courseMapper.toCourseResponse(course);
    }


    public List<CourseResponse> getAllCourses() {

        return courseRepository.findAllByApproveStatus(ApproveStatus.APPROVED)
                .stream()
                .map(courseMapper::toCourseResponse)
                .toList();
    }


    public List<CourseResponse> getMyEnrolledCourses() {
        String username = authUtils.getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Profile profile = user.getProfile();
        if (profile == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        List<Enrollment> enrollments = enrollRepository.findByProfile(profile);
        return enrollments.stream()
                .map(Enrollment::getCourse)
                .map(courseMapper::toCourseResponse)
                .toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR')")
    @Cacheable( value = "getCoursesByStatus",
            key = "#status + '-' + #page + '-' + #size + '-' + @courseSecurity.getCurrentUsername()")
    public Page<CourseResponse> getCoursesByStatus(String status, int page, int size) {
        String username = authUtils.getCurrentUsername();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // Lấy authorities hiện tại
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin      = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isInstructor = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_INSTRUCTOR"));

        if (isAdmin) {
            if (status.equalsIgnoreCase("all")) {
                // Trả về tất cả các khóa học, không phân biệt trạng thái
                return courseRepository.findByApproveStatusIn(Arrays.asList(ApproveStatus.PROCESSING, ApproveStatus.APPROVED, ApproveStatus.REJECTED), pageable)
                        .map(courseMapper::toCourseResponse);
            } else {
                // Nếu có status cụ thể, chuyển đổi nó thành ApproveStatus
                try {
                    ApproveStatus approveStatus = ApproveStatus.valueOf(status);
                    return courseRepository.findByApproveStatus(approveStatus, pageable)
                            .map(courseMapper::toCourseResponse);
                } catch (IllegalArgumentException e) {
                    throw new AppException(ErrorCode.COURSE_NOT_EXISTED);
                }
            }
        }

        if (isInstructor) {
            try {
                Instructor instructor = userRepository.findInstructorByUsername(username)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

                Page<Course> rawPage;
                if (status.equalsIgnoreCase("DRAFT")) {
                    CourseStatus courseStatus = CourseStatus.valueOf(status);
                    rawPage = courseRepository
                            .findByInstructorCourse_InstructorAndIsActive(instructor, courseStatus, pageable);
                } else {
                    ApproveStatus approveStatus = ApproveStatus.valueOf(status);
                    rawPage = courseRepository
                            .findByInstructorCourse_InstructorAndApproveStatus(instructor, approveStatus, pageable);
                }

                // Hiển thị khi giảng viên là owner HOẶC đã chấp thuận và đang Active
                List<Course> filtered = rawPage.stream()
                        .filter(course -> course.getInstructorCourse().stream()
                                .anyMatch(ic -> ic.getInstructor().getId().equals(instructor.getId()) &&
                                        (ic.isOwner() || ic.getStatus() == InviteStatus.ACCEPTED) &&
                                        ic.getIsActive() == ActiveStatus.Active))
                        .toList();

                Page<Course> filteredPage = new PageImpl<>(filtered, pageable, filtered.size());
                return filteredPage.map(courseMapper::toCourseResponse);
            } catch (IllegalArgumentException e) {
                throw new AppException(ErrorCode.COURSE_NOT_EXISTED);
            } catch (AppException e) {
                throw new RuntimeException(e);
            }
        }
        throw new AccessDeniedException("Role not supported");
    }

//    @PreAuthorize("hasRole('INSTRUCTOR')")
//    public Page<CourseResponse> getInstructorCourses (String status, int page, int size)
//    {
//        String username = authUtils.getCurrentUsername();
//
//        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
//
//        CourseStatus enumStatus = CourseStatus.valueOf(status.toUpperCase());
//
//        Instructor instructor = userRepository.findInstructorByUsername(username).orElseThrow(
//                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
//        );
//
//        Page<Course> courses = courseRepository.findByInstructorCourse_InstructorAndIsActive(instructor, enumStatus, pageable);
//        return courses.map(courseMapper::toCourseResponse);
//    }
//
    @PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR')")
    public CourseProcessingResponse checkCourseProcessing (String courseId)
    {
        String username = authUtils.getCurrentUsername();
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new AppException(ErrorCode.COURSE_NOT_EXISTED)
        );
        switch (course.getApproveStatus()) {
            case APPROVED:
                return CourseProcessingResponse.builder()
                        .courseStatus("APPROVED")
                        .courseId(courseId)
                        .build();
            case PROCESSING:
                return CourseProcessingResponse.builder()
                        .courseStatus("PROCESSING")
                        .courseId(courseId)
                        .build();
            case REJECTED:
                return CourseProcessingResponse.builder()
                        .courseStatus("REJECTED")
                        .courseId(courseId)
                        .build();
            default:
                return CourseProcessingResponse.builder()
                        .courseStatus("")
                        .courseId(courseId)
                        .build();
        }
    }

    

    public CourseResponse getCourseByCourseId(String courseId) {
        // Public endpoint – return course with sections & lessons but WITHOUT lesson content
        Course course = courseRepository.findCourseById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXISTED));

        // Load instructor metadata (maintains existing behaviour)
        List<InstructorCourse> instructorCourses = instructorCourseRepository.findInstructorsWithPermissionsByCourseId(courseId)
                .stream()
                .sorted(Comparator.comparing(InstructorCourse::isOwner).reversed())
                .toList();
        course.setInstructorCourse(new LinkedHashSet<>(instructorCourses));

        // Build nested sections/lessons; strip lesson `content`
        List<SectionResponse> sectionResponses = course.getSections() == null ? List.of() : course.getSections().stream()
                .sorted(Comparator.comparingInt(Section::getIndex))
                .map(sec -> {
                    SectionResponse sr = sectionMapper.toSectionResponse(sec);
                    List<LessonResponse> lessons = sec.getLessons() == null ? List.of() : sec.getLessons().stream()
                            .sorted(Comparator.comparingInt(Lesson::getIndex))
                            .map(les -> {
                                LessonResponse lr = lessonMapper.toLessonResponse(les);
                                lr.setContent(null); // hide lesson content for public
                                return lr;
                            }).toList();
                    sr.setLessons(lessons);
                    List<AssignmentResponse> assignments = assignmentService.getAssignmentsBySection(sec.getId())
                            .stream()
                            .sorted(Comparator.comparingInt(AssignmentResponse::getIndex))
                            .toList();

                    sr.setAssignments(assignments);
                    return sr;
                }).toList();

        CourseResponse resp = courseMapper.toCourseResponse(course);
        resp.setSections(sectionResponses);
        return resp;
    }

    public void deleteCourse(String nameCourse) {
            Course course = courseRepository.findByName(nameCourse).orElseThrow(
                    ()-> new AppException(ErrorCode.COURSE_NOT_EXISTED)
            );
            courseRepository.delete(course);
    }


    @PreAuthorize("@authorizationService.canViewCourse(#courseId)")
    public CourseResponse updateCourse(String courseId, CourseUpdateRequest courseUpdateRequest, MultipartFile imageUrl,
                                       MultipartFile videoUrl) {
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new AppException(ErrorCode.COURSE_NOT_EXISTED)
        );
        if (imageUrl != null && !imageUrl.isEmpty()) {
            String saveImageUrl = fileStorageService.saveImageCourse(imageUrl, "courses/" + courseId);
            course.setImageUrl(saveImageUrl);
        }
        if (videoUrl != null && !videoUrl.isEmpty()) {
            String saveVideoUrl = fileStorageService.saveVideoCourse(videoUrl, "courses/videos/" + courseId);
            course.setVideoUrl(saveVideoUrl);
        }

        if(courseUpdateRequest != null) {
            if( courseUpdateRequest.getCategory() != null ) {
                Category category = categoryRepository.findByName(courseUpdateRequest.getCategory()).orElseThrow(
                        () -> new AppException(ErrorCode.COURSE_NOT_EXISTED)
                );
                course.setCategory(category);
            }

            if (courseUpdateRequest.getTopic() != null) {
                Topic topic = topicRepository.findByName(courseUpdateRequest.getTopic())
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy topic"));
                course.setTopic(topic);
            }

            if (courseUpdateRequest.getLevelCourse() != null) {
                Level level = levelRepository.findLevelById(courseUpdateRequest.getLevelCourse())
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy topic"));
                course.setLevelCourse(level);
            }
            courseMapper.updateCourse(course,courseUpdateRequest);
        }
        course = courseRepository.save(course);

        return courseMapper.toCourseResponse(course);
    }

}
