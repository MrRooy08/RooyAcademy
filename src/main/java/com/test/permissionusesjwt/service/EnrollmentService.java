package com.test.permissionusesjwt.service;


import com.test.permissionusesjwt.authUtils.AuthUtils;
import com.test.permissionusesjwt.dto.request.EnrollmentRequest;
import com.test.permissionusesjwt.dto.response.EnrollmentResponse;
import com.test.permissionusesjwt.entity.*;
import com.test.permissionusesjwt.enums.ApproveStatus;
import com.test.permissionusesjwt.enums.CourseStatus;
import com.test.permissionusesjwt.enums.FinishStatus;
import com.test.permissionusesjwt.enums.OrderStatus;
import com.test.permissionusesjwt.exception.AppException;
import com.test.permissionusesjwt.exception.ErrorCode;
import com.test.permissionusesjwt.mapper.EnrollmentMapper;
import com.test.permissionusesjwt.repository.CourseRepository;
import com.test.permissionusesjwt.repository.EnrollRepository;
import com.test.permissionusesjwt.repository.ProfileRepository;
import com.test.permissionusesjwt.repository.ProgressRepository;
import com.test.permissionusesjwt.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EnrollmentService {

    EnrollmentMapper enrollmentMapper;
    CourseRepository courseRepository;
    EnrollRepository enrollRepository;
    ProfileRepository profileRepository;
    ProgressRepository progressRepository;
    AuthUtils authUtils;
    private final UserRepository userRepository;

    @Transactional
    public EnrollmentResponse create(EnrollmentRequest request) {
        Enrollment enroll = new Enrollment();
        String username = authUtils.getCurrentUsername();
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        enroll = enrollCourse(user,request.getCourseId());
        return enrollmentMapper.toEnrollmentResponse(enroll);
    }

    @Transactional
    public Enrollment enrollCourse(User user, String courseId) {

        Profile profile = profileRepository.findById(user.getProfile().getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Course course = courseRepository.findById(courseId)
                .filter(c -> c.getApproveStatus() == ApproveStatus.APPROVED
                        && c.getIsActive() == CourseStatus.PUBLIC)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXISTED));

        // Nếu khóa học có phí, kiểm tra user đã thanh toán chưa
        if (course.getPrice().getPrice().compareTo(BigDecimal.ZERO) > 0) {
            boolean hasPaidOrder = user.getOrders().stream()
                    .filter(order -> order.getStatus() == OrderStatus.SUCCESS)
                    .flatMap(order -> order.getItems().stream())
                    .anyMatch(item -> item.getCourse().getId().equals(courseId));

            if (!hasPaidOrder) {
                throw new AppException(ErrorCode.ORDER_NOT_PAID);
            }
        }

        // Kiểm tra đã enroll chưa
        if (enrollRepository.existsByProfileAndCourse(profile, course)) {
            throw new AppException(ErrorCode.ENROLLMENT_EXISTED);
        }

        // Tạo enrollment + progress
        Enrollment enroll = Enrollment.builder()
                .profile(profile)
                .course(course)
                .isFinished(FinishStatus.UNFINISHED)
                .build();

        Progress progress = Progress.builder()
                .enrollment(enroll)
                .finishStatus(FinishStatus.UNFINISHED)
                .build();

        enroll.getProgresses().add(progress);
        return enrollRepository.save(enroll);
    }

}
