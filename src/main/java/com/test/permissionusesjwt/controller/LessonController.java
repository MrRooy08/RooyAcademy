package com.test.permissionusesjwt.controller;

import com.test.permissionusesjwt.dto.request.ApiResponse;
import com.test.permissionusesjwt.dto.request.LessonRequest;
import com.test.permissionusesjwt.dto.request.LessonUpdateRequest;
import com.test.permissionusesjwt.dto.response.LessonResponse;
import com.test.permissionusesjwt.entity.Lesson;
import com.test.permissionusesjwt.service.LessonService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/lesson")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class LessonController {

    private final LessonService lessonService;
    private final com.test.permissionusesjwt.mapper.LessonMapper lessonMapper;

    @PostMapping( value = "/create-lesson", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<LessonResponse> createLesson(
            @RequestParam("sectionId") String sectionId,
            @RequestPart(value = "files", required = false) MultipartFile[] files,
            @RequestPart(value = "videoFile", required = false) MultipartFile video,
            @RequestPart("lesson")  LessonRequest request) {
        request.setSection(sectionId);
        return ApiResponse.<LessonResponse>builder()
                .result(lessonService.createLesson(request,files,video))
                .build();
    }

    @PostMapping( value = "/update-lesson", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<LessonResponse> updateLesson(
            @RequestParam("lessonId") String lessonId,
            @RequestPart(value = "files", required = false) MultipartFile[] files,
            @RequestPart(value = "videoFile", required = false) MultipartFile video,
            @RequestPart("lesson") LessonUpdateRequest request) {
        request.setId(lessonId);
        return ApiResponse.<LessonResponse>builder()
                .result(lessonService.updateLesson(request,files,video))
                .build();
    }

    @PreAuthorize("@authorizationService.canViewSection(#sectionId)")
    @GetMapping("/get-lesson-by-section")
    ApiResponse<List<LessonResponse>> getLessonBySection (@RequestParam("sectionId") String sectionId) {
        List<Lesson> lessons = lessonService.getLessonBySection(sectionId);
        List<LessonResponse> responses = lessons.stream().map(lessonMapper::toLessonResponse).toList();
        return ApiResponse.<List<LessonResponse>>builder()
                .result(responses)
                .build();
    }

//    @PostMapping(value = "/upload-media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> uploadLessonMedia(
//            @RequestParam("lessonId") String lessonId,
//            @RequestPart("files") MultipartFile[] files,
//            @RequestPart("lesson") LessonRequest request
//    ) {
//        List<Media> saved = fileStorageService.saveLessonResources(files, "lessons/" + lessonId);
//
//        // Trả về danh sách đơn giản kiểu Map (tên file + URL)
//        List<Map<String, Object>> result = saved.stream().map(media -> {
//            Map<String, Object> item = new HashMap<>();
//            item.put("name", media.getName());
//            item.put("url", media.getUrl());
//            item.put("size", media.getSize());
//            return item;
//        }).toList();
//
//        // Trả về cả lesson request và danh sách file
//        Map<String, Object> response = new HashMap<>();
//        response.put("lesson", request);
//        response.put("files", result);
//
//        return ResponseEntity.ok(Map.of("resources", response));
//    }


}
