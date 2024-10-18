package com.test.permissionusesjwt.service;

import com.test.permissionusesjwt.dto.request.LessonRequest;
import com.test.permissionusesjwt.dto.request.LevelRequest;
import com.test.permissionusesjwt.dto.response.LessonResponse;
import com.test.permissionusesjwt.dto.response.LevelResponse;
import com.test.permissionusesjwt.entity.Course;
import com.test.permissionusesjwt.entity.Lesson;
import com.test.permissionusesjwt.exception.AppException;
import com.test.permissionusesjwt.exception.ErrorCode;
import com.test.permissionusesjwt.mapper.LessonMapper;
import com.test.permissionusesjwt.repository.CourseRepository;
import com.test.permissionusesjwt.repository.LessonRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LessonService {
    LessonMapper lessonMapper;
    LessonRepository lessonRepository;
    CourseRepository courseRepository;

    public LessonResponse createLesson(LessonRequest lessonRequest) {
        Lesson lesson = lessonMapper.toLesson(lessonRequest);
         Course course = courseRepository.findByName(lessonRequest.getCourse()).orElseThrow(
                () -> new AppException(ErrorCode.COURSE_NOT_EXISTED)
        );

        lesson.setCourse(course);
        try {
            lesson = lessonRepository.save(lesson);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.LESSON_NOT_EXISTED);
        }

        return lessonMapper.toLessonResponse(lesson);
    }

//    public void deleteByName(String name) {
//        Lesson lesson = lessonRepository.findLevelByName(name).orElseThrow(
//                ()-> new AppException(ErrorCode.LEVEL_NOT_EXISTED)
//        );
//        levelRepository.delete(level);
//    }


//    public LevelResponse updateLevel (String name, LevelRequest levelRequest) {
//        Level level = levelRepository.findLevelByName(name).orElseThrow(
//                () -> new AppException(ErrorCode.LEVEL_NOT_EXISTED)
//        );
//
//        levelMapper.updateLevel(level, levelRequest);
//        return levelMapper.toLevelResponse(levelRepository.save(level));
//    }

    public List<LessonResponse> getAllLessons() {
        return lessonRepository.findAll().stream()
                .map(lessonMapper::toLessonResponse)
                .toList();
    }
}
