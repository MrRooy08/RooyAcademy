package com.test.permissionusesjwt.controller;


import com.test.permissionusesjwt.dto.request.ApiResponse;
import com.test.permissionusesjwt.dto.request.SectionRequest;
import com.test.permissionusesjwt.dto.response.SectionResponse;
import com.test.permissionusesjwt.exception.AppException;
import com.test.permissionusesjwt.exception.ErrorCode;
import com.test.permissionusesjwt.security.AuthorizationService;
import com.test.permissionusesjwt.service.SectionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/section")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC,makeFinal = true)
@Slf4j
public class SectionController {

    SectionService sectionService;
    private final AuthorizationService authorizationService;

    @GetMapping
    ApiResponse<List<SectionResponse>> getAllLessons() {
        return ApiResponse.<List<SectionResponse>>builder()
                .result(sectionService.getAllLessons())
                .build();
    }

    @GetMapping("/get-section-by-course/{courseId}")
    ApiResponse<List<SectionResponse>> getSectionByCourse(@PathVariable String courseId) {
        return ApiResponse.<List<SectionResponse>>builder()
                .result(sectionService.getSectionByCourseId(courseId))
                 .build();
    }


    @PostMapping("/create-section/{courseId}")
    ApiResponse<SectionResponse> createSection(@PathVariable String courseId, @RequestBody SectionRequest sectionRequest) {
        if(!authorizationService.canViewCourse(courseId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        };
        return ApiResponse.<SectionResponse>builder()
                .result(sectionService.createSection(courseId, sectionRequest))
                .build();
    }
    @PostMapping("/update-section/{sectionId}")
    ApiResponse<SectionResponse> updateSection(@PathVariable String sectionId, @RequestBody SectionRequest sectionRequest) {
        return ApiResponse.<SectionResponse>builder()
                .result(sectionService.updateSection(sectionId, sectionRequest))
                .build();
    }



//    @DeleteMapping("/{nameCourse}")
//    ApiResponse<Void> deleteCourse(@PathVariable String nameCourse) {
//        lessonService.deleteCourse(nameCourse);
//        return ApiResponse.<Void>builder()
//                .build();
//    }
}
