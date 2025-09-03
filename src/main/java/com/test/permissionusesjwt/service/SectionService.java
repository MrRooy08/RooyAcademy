package com.test.permissionusesjwt.service;

import com.test.permissionusesjwt.dto.request.SectionRequest;
import com.test.permissionusesjwt.dto.response.LessonResponse;
import com.test.permissionusesjwt.dto.response.SectionResponse;
import com.test.permissionusesjwt.entity.Course;
import com.test.permissionusesjwt.entity.Lesson;
import com.test.permissionusesjwt.entity.Section;
import com.test.permissionusesjwt.exception.AppException;
import com.test.permissionusesjwt.exception.ErrorCode;
import com.test.permissionusesjwt.mapper.SectionMapper;
import com.test.permissionusesjwt.repository.CourseRepository;
import com.test.permissionusesjwt.repository.SectionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SectionService {
    SectionMapper sectionMapper;
    SectionRepository sectionRepository;
    CourseRepository courseRepository;
    private final LessonService lessonService;



    public SectionResponse createSection(String courseId, SectionRequest sectionRequest) {
        Section section = sectionMapper.toSection(sectionRequest);
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new AppException(ErrorCode.COURSE_NOT_EXISTED)
        );

        section.setCourse(course);
        try {
            section = sectionRepository.save(section);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.valueOf("Đã tôn tại vị trí "));
        }

        return sectionMapper.toSectionResponse(section);
    }


    @PreAuthorize("@authorizationService.canViewSection(#sectionId)")
    public SectionResponse updateSection(String sectionId, SectionRequest sectionRequest) {
        Section section = sectionRepository.findById(sectionId).orElseThrow(
                () -> new AppException(ErrorCode.LEVEL_NOT_EXISTED)
        );
        sectionMapper.updateSectionFromDto(sectionRequest, section);
        try {
            section = sectionRepository.save(section);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.valueOf("Đã tôn tại vị trí "));
        }

        return sectionMapper.toSectionResponse(section);
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

    public List<SectionResponse> getAllLessons() {
        return sectionRepository.findAll().stream()
                .map(sectionMapper::toSectionResponse)
                .toList();
    }

    @PreAuthorize("@authorizationService.canViewCourse(#courseId)")
    public List<SectionResponse> getSectionByCourseId (String courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new AppException(ErrorCode.COURSE_NOT_EXISTED)
        );
        List<Section> sections = sectionRepository.findByCourseId(course.getId());
        for (Section section : sections) {
            List<Lesson> lesson = lessonService.getLessonBySection(section.getId());
            lesson.sort(Comparator.comparingInt(Lesson::getIndex));
            section.setLessons(new LinkedHashSet<>(lesson));
        }
        sections.sort(Comparator.comparingInt(Section::getIndex));
        return sections.stream()
                .map(sectionMapper::toSectionResponse)
                .toList();

    }
}
