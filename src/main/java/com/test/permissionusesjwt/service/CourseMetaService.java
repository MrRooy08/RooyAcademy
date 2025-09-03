package com.test.permissionusesjwt.service;

import com.test.permissionusesjwt.authUtils.AuthUtils;
import com.test.permissionusesjwt.dto.request.CourseMetaItem;
import com.test.permissionusesjwt.dto.request.CourseMetaRequest;
import com.test.permissionusesjwt.dto.response.CourseMetaResponse;
import com.test.permissionusesjwt.entity.*;
import com.test.permissionusesjwt.enums.TypeCourseMeta;
import com.test.permissionusesjwt.exception.AppException;
import com.test.permissionusesjwt.exception.ErrorCode;
import com.test.permissionusesjwt.repository.CourseMetaRepository;
import com.test.permissionusesjwt.repository.CourseRepository;
import com.test.permissionusesjwt.repository.InstructorCourseRepository;
import com.test.permissionusesjwt.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CourseMetaService {
    UserRepository  userRepository;
    InstructorCourseRepository instructorCourseRepository;
    AuthUtils authUtils;
    private final CourseMetaRepository courseMetaRepository;
    private final CourseRepository courseRepository;

    @PreAuthorize("hasRole('INSTRUCTOR')")
    public CourseMetaResponse createCourseMeta(String courseId, CourseMetaRequest request) {
        String username = authUtils.getCurrentUsername();

        // Kiểm tra thông tin instructor
        Instructor instructor = userRepository.findInstructorByUsername(username).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        // Kiểm tra thông tin course
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new AppException(ErrorCode.LEVEL_NOT_EXISTED)
        );

        // Kiểm tra instructor có quyền với course này
        InstructorCourse isExisted = instructorCourseRepository.findByCourseIdAndInstructor(courseId, instructor)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXISTED));

        if (!isExisted.isOwner()) {
            boolean hasPermission = isExisted.getPermissions().stream()
                    .map(Permission::getId)
                    .anyMatch(ma -> ma.equals("QL"));
            if (!hasPermission || isExisted.getIsActive().equals("Inactive")) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }
        }

        List<CourseMeta> metaList = new ArrayList<>();

        // Xử lý dữ liệu meta
        request.getMeta().forEach((type, items) -> {
            for (CourseMetaItem item : items) {
                if (item.getId() != null && !item.getId().isEmpty()) {
                    // Nếu có ID, thực hiện cập nhật
                    CourseMeta existingMeta = courseMetaRepository.findById(item.getId())
                            .orElseThrow(() -> new AppException(ErrorCode.LEVEL_NOT_EXISTED));

                    existingMeta.setContent(item.getContent());
                    existingMeta.setIndex(item.getIndex());
                    existingMeta.setType(TypeCourseMeta.valueOf(type.toUpperCase()));
                    metaList.add(existingMeta);
                } else {
                    // Nếu không có ID, tạo mới
                    metaList.add(CourseMeta.builder()
                            .course(course)
                            .type(TypeCourseMeta.valueOf(type.toUpperCase()))
                            .content(item.getContent())
                            .index(item.getIndex())
                            .build());
                }
            }
        });

        // Lưu hoặc cập nhật tất cả meta
        courseMetaRepository.saveAll(metaList);

        // Trả về dữ liệu response
        return CourseMetaResponse.builder()
                .meta(request.getMeta())
                .build();
    }


    public CourseMetaResponse getMetaByCourseId (String courseId)
    {
//        String username = authUtils.getCurrentUsername();
//        List<String> roles = authUtils.getRolesFromToken();

        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new AppException(ErrorCode.LEVEL_NOT_EXISTED)
        );


//        if(roles.contains("INSTRUCTOR")) {
//            Instructor instructor = userRepository.findInstructorByUsername(username).orElseThrow(
//                    () -> new AppException(ErrorCode.USER_NOT_EXISTED)
//            );
//
//            InstructorCourse isExisted = instructorCourseRepository.findByCourseIdAndInstructor(courseId, instructor)
//                    .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXISTED));
//            if (!isExisted.isOwner()) {
//                boolean hasPermission = isExisted.getPermissions().stream()
//                        .map(Permission::getId)
//                        .anyMatch(ma -> ma.equals("QL"));
//                if (!hasPermission || isExisted.getIsActive().equals("Inactive")) {
//                    throw new AppException(ErrorCode.ENROLLMENT_EXISTED);
//                }
//            }
//        }

        List<CourseMeta> courseMeta =  courseMetaRepository.findAllByCourseId(course.getId()).orElseThrow(
                () -> new AppException(ErrorCode.LEVEL_NOT_EXISTED)
        );
            Map<String,List<CourseMetaItem>> meta = new HashMap<>();
            courseMeta.forEach(courseMetaData -> {
                CourseMetaItem temp = CourseMetaItem
                        .builder()
                        .id(courseMetaData.getId())
                        .content(courseMetaData.getContent())
                        .index(courseMetaData.getIndex())
                        .build();

                meta.computeIfAbsent(String.valueOf(courseMetaData.getType()),k -> new ArrayList<>()).add(temp);
            });
            meta.forEach((key, value) -> {
                value.sort(Comparator.comparingInt(CourseMetaItem::getIndex));
            });
        return CourseMetaResponse.builder()
                .meta(meta)
                .build();
    }


}
