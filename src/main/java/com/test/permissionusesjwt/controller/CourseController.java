package com.test.permissionusesjwt.controller;

import com.test.permissionusesjwt.dto.request.ApiResponse;
import com.test.permissionusesjwt.dto.request.CourseRequest;
import com.test.permissionusesjwt.dto.request.CourseUpdateRequest;
import com.test.permissionusesjwt.dto.response.CourseResponse;
import com.test.permissionusesjwt.repository.CourseRepository;
import com.test.permissionusesjwt.service.CourseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class CourseController {
    CourseService courseService;

    @GetMapping
    ApiResponse<List<CourseResponse>> getAllCourses() {
        return ApiResponse.<List<CourseResponse>>builder()
                .result(courseService.getAllCourses())
                .build();
    }

    @PostMapping("/create-course")
    ApiResponse<CourseResponse> createCourse(@RequestBody CourseRequest courseRequest) {
        return ApiResponse.<CourseResponse>builder()
                .result(courseService.createCourse(courseRequest))
                .build();
    }

    @DeleteMapping("/{nameCourse}")
    ApiResponse<Void> deleteCourse(@PathVariable String nameCourse) {
        courseService.deleteCourse(nameCourse);
        return ApiResponse.<Void>builder()
                .build();
    }

    @PutMapping("/{name}")
    ApiResponse<CourseResponse> updateCourse(
            @PathVariable String name,
            @RequestBody CourseUpdateRequest courseRequest)
    {
        return ApiResponse.<CourseResponse>builder()
                    .result(courseService.updateCourse(name,courseRequest))
                    .build();
    }

    @GetMapping("/get-course-name")
    ApiResponse<CourseResponse> getCourseByName (@RequestParam String name) {
        return ApiResponse.<CourseResponse>builder()
                .result(courseService.getCourseByName(name))
                .build();
    }

}
