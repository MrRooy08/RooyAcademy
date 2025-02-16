package com.test.permissionusesjwt.service;


import com.test.permissionusesjwt.dto.request.CourseRequest;
import com.test.permissionusesjwt.dto.request.CourseUpdateRequest;
import com.test.permissionusesjwt.dto.response.CourseResponse;
import com.test.permissionusesjwt.entity.Course;
import com.test.permissionusesjwt.entity.Lesson;
import com.test.permissionusesjwt.entity.Level;
import com.test.permissionusesjwt.exception.AppException;
import com.test.permissionusesjwt.exception.ErrorCode;
import com.test.permissionusesjwt.mapper.CourseMapper;
import com.test.permissionusesjwt.repository.CourseRepository;
import com.test.permissionusesjwt.repository.LessonRepository;
import com.test.permissionusesjwt.repository.LevelRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CourseService {
    CourseRepository courseRepository;
    CourseMapper courseMapper;
    LevelRepository levelRepository;

    public CourseResponse createCourse(CourseRequest courseRequest) {
        Course course = courseMapper.toCourse(courseRequest);
        Level level = levelRepository.findLevelByName(courseRequest.getLevelCourse()).orElseThrow(
                ()-> new AppException(ErrorCode.LEVEL_NOT_EXISTED)
        );
        course.setLevelCourse(level);

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
        return courseRepository.findAll()
                .stream()
                .map(courseMapper::toCourseResponse)
                .toList();
    }

    public CourseResponse getCourseByName(String name) {
        Course course = courseRepository.findByName(name).orElseThrow(
                () -> new AppException(ErrorCode.COURSE_NOT_EXISTED)
        );
        return courseMapper.toCourseResponse(course);
    }




    public void deleteCourse(String nameCourse) {
            Course course = courseRepository.findByName(nameCourse).orElseThrow(
                    ()-> new AppException(ErrorCode.COURSE_NOT_EXISTED)
            );
            courseRepository.delete(course);
    }

    public CourseResponse updateCourse(String name, CourseUpdateRequest courseRequest) {
        Course course = courseRepository.findByName(name).orElseThrow(
                () -> new AppException(ErrorCode.COURSE_NOT_EXISTED)
        );

        courseMapper.updateCourse(course,courseRequest);

        return courseMapper.toCourseResponse(courseRepository.save(course));
    }
}
