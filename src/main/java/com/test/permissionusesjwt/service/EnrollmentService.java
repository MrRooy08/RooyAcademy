package com.test.permissionusesjwt.service;


import com.test.permissionusesjwt.dto.request.EnrollmentRequest;
import com.test.permissionusesjwt.dto.request.LessonRequest;
import com.test.permissionusesjwt.dto.response.EnrollmentResponse;
import com.test.permissionusesjwt.dto.response.LessonResponse;
import com.test.permissionusesjwt.entity.Course;
import com.test.permissionusesjwt.entity.Enrollment;
import com.test.permissionusesjwt.entity.Lesson;
import com.test.permissionusesjwt.entity.User;
import com.test.permissionusesjwt.exception.AppException;
import com.test.permissionusesjwt.exception.ErrorCode;
import com.test.permissionusesjwt.mapper.EnrollmentMapper;
import com.test.permissionusesjwt.repository.CourseRepository;
import com.test.permissionusesjwt.repository.EnrollRepository;
import com.test.permissionusesjwt.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EnrollmentService {

    EnrollmentMapper enrollmentMapper;
    CourseRepository courseRepository;
    EnrollRepository enrollRepository;
    UserRepository userRepository;

    public EnrollmentResponse create(EnrollmentRequest request) {

        User user = userRepository.findById(request.getUserId()).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        Course course = courseRepository.findById(request.getCourseId()).orElseThrow(
                () -> new AppException(ErrorCode.COURSE_NOT_EXISTED)
        );

        if (enrollRepository.existsByUserIdAndCourseId(user,course)) {
            throw new AppException(ErrorCode.ENROLLMENT_EXISTED);
        }

        Enrollment enroll = enrollmentMapper.toEnrollment(request);
        enroll.setUserId(user);
        enroll.setCourseId(course);

        try {
            enroll = enrollRepository.save(enroll);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.ENROLLMENT_EXISTED);
        }

        return enrollmentMapper.toEnrollmentResponse(enroll);
    }

}
