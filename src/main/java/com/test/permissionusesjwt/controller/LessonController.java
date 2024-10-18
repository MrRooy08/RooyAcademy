package com.test.permissionusesjwt.controller;


import com.test.permissionusesjwt.dto.request.ApiResponse;
import com.test.permissionusesjwt.dto.request.LessonRequest;
import com.test.permissionusesjwt.dto.response.LessonResponse;
import com.test.permissionusesjwt.service.LessonService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lesson")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class LessonController {

    LessonService lessonService;

    @GetMapping
    ApiResponse<List<LessonResponse>> getAllLessons() {
        return ApiResponse.<List<LessonResponse>>builder()
                .result(lessonService.getAllLessons())
                .build();
    }

    @PostMapping("/create-lesson")
    ApiResponse<LessonResponse> createLesson(@RequestBody LessonRequest lessonRequest) {
        return ApiResponse.<LessonResponse>builder()
                .result(lessonService.createLesson(lessonRequest))
                .build();
    }

//    @DeleteMapping("/{nameCourse}")
//    ApiResponse<Void> deleteCourse(@PathVariable String nameCourse) {
//        lessonService.deleteCourse(nameCourse);
//        return ApiResponse.<Void>builder()
//                .build();
//    }
}
