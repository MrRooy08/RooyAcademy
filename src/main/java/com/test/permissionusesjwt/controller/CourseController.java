package com.test.permissionusesjwt.controller;

import com.test.permissionusesjwt.dto.request.*;
import com.test.permissionusesjwt.dto.request.CoursePriceUpdateRequest;
import com.test.permissionusesjwt.dto.response.*;
import com.test.permissionusesjwt.service.CourseMetaService;
import com.test.permissionusesjwt.service.CourseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class CourseController {
    CourseService courseService;
    CourseMetaService courseMetaService;

    @GetMapping
    ApiResponse<List<CourseResponse>> getAllCourses() {
        return ApiResponse.<List<CourseResponse>>builder()
                .result(courseService.getAllCourses())
                .build();
    }

    @PostMapping("/create-course-draft")
    ApiResponse<CourseResponse> createDaftCourse(@RequestBody CourseDraftRequest courseRequest) {
        return ApiResponse.<CourseResponse>builder()
                .result(courseService.createDraftCourse(courseRequest))
                .build();
    }

    @PostMapping("/create-course")
    ApiResponse<CourseResponse> createCourse(@RequestBody CourseRequest courseRequest) {
        return ApiResponse.<CourseResponse>builder()
                .result(courseService.createCourse(courseRequest))
                .build();
    }

    @PostMapping("/add-co-instructor/{courseId}")
    ApiResponse<HandleInstructorDto> addCoInstructor(@PathVariable String courseId , @RequestBody HandleInstructorDto request) {
        return ApiResponse.<HandleInstructorDto>builder()
                .result(courseService.addCoInstructor(courseId,request))
                .build();
    }

    @PostMapping("/create-course-meta/{courseId}")
    ApiResponse<CourseMetaResponse> createCourseMeta(@PathVariable String courseId , @RequestBody CourseMetaRequest request) {
        return ApiResponse.<CourseMetaResponse>builder()
                .result(courseMetaService.createCourseMeta(courseId,request))
                .build();
    }

    @GetMapping("/get-course-meta/{courseId}")
    ApiResponse<CourseMetaResponse> getCourseMeta(@PathVariable String courseId) {
        return ApiResponse.<CourseMetaResponse>builder()
                .result(courseMetaService.getMetaByCourseId(courseId))
                .build();
    }

    @GetMapping("/get-course-by-id/{courseId}")
    ApiResponse<CourseResponse> getCourseById(@PathVariable String courseId) {
        return ApiResponse.<CourseResponse>builder()
                .result(courseService.getCourseByCourseId(courseId))
                .build();
    }

    @GetMapping("/get-course-by-status")
    ApiResponse<PagedResponse<CourseResponse>> getCourseByStatus(@RequestParam String status, @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam (defaultValue = "5") int size) {
        PagedResponse<CourseResponse> response = courseService.getCoursesByStatus(status, page, size);
        return ApiResponse.<PagedResponse<CourseResponse>>builder()
                .message("Successfully")
                .result(response)
                .build();
    }

    @GetMapping("/get-course-price/{courseId}")
    ApiResponse<String> getCoursePrice(@PathVariable String courseId) {
        return ApiResponse.<String>builder()
                .result(courseService.getCoursePriceId(courseId))
                .build();
    }

    @GetMapping("/my-enrolled-courses")
    ApiResponse<List<CourseResponse>> getMyEnrolledCourses() {
        return ApiResponse.<List<CourseResponse>>builder()
                .result(courseService.getMyEnrolledCourses())
                .build();
    }

    @GetMapping("/check-course-processing/{courseId}")
    ApiResponse<CourseProcessingResponse> checkCourseProcessing(@PathVariable String courseId) {
        return ApiResponse.<CourseProcessingResponse>builder()
                .result(courseService.checkCourseProcessing(courseId))
                .build();
    }


//    @GetMapping("/get-courses-instructor")
//    ApiResponse<PagedResponse<CourseResponse>> getInstructorCourses(
//            @RequestParam String status, @RequestParam(defaultValue = "0") int page,
//            @RequestParam (defaultValue = "5") int size
//    ) {
//        Page<CourseResponse> pageResponse = courseService.getInstructorCourses(status, page, size);
//        PagedResponse<CourseResponse> response = paginationUtils.mapPageToPagedResponse(pageResponse);
//
//        return ApiResponse.<PagedResponse<CourseResponse>>builder()
//                .message("Successfully")
//                .result(response)
//                .build();
//    }


    @DeleteMapping("/{nameCourse}")
    ApiResponse<Void> deleteCourse(@PathVariable String nameCourse) {
        courseService.deleteCourse(nameCourse);
        return ApiResponse.<Void>builder()
                .build();
    }

    @PostMapping("/update-course-price/{courseId}")
    ApiResponse<ResponseEntity<String>> updateCoursePrice(@PathVariable String courseId, @RequestBody CoursePriceUpdateRequest request) {
        return ApiResponse.<ResponseEntity<String>>builder()
                .result(courseService.updateCoursePrice(courseId, request))
                .build();
    }

    @PutMapping(path = "/update-course-overview", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<CourseResponse> updateCourse(
            @RequestParam ("courseId") String courseId,
            @RequestPart (value = "body", required = false) CourseUpdateRequest courseRequest,
            @RequestPart (value = "imageUrl", required = false) MultipartFile imageUrl,
            @RequestPart (value = "videoUrl", required = false) MultipartFile videoUrl)
    {
        return ApiResponse.<CourseResponse>builder()
                    .result(courseService.updateCourse(courseId,courseRequest,imageUrl,videoUrl))
                    .build();
    }



    @PostMapping ("/submit-public/{courseId}")
    ApiResponse<ResponseEntity<Void>> submitPublicCourse(@PathVariable String courseId) {
        return ApiResponse.<ResponseEntity<Void>>builder()
                .result(courseService.submitPublicCourse(courseId))
                .build();
    }

    @PostMapping ("/approve-public/{courseId}")
    ApiResponse<ResponseEntity<Void>> approvePublicCourse(@PathVariable String courseId) {
        return ApiResponse.<ResponseEntity<Void>>builder()
                .result(courseService.approvePublicCourse(courseId))
                .build();
    }

    /**
     * Kiểm tra xem người dùng hiện tại có từng là giảng viên của khóa học hay không
     */
    //    @GetMapping("/check-user-was-instructor/{courseId}")
    //    ApiResponse<Boolean> checkCurrentUserWasInstructor(@PathVariable String courseId) {
    //        boolean wasInstructor = courseService.hasCurrentUserBeenInstructorOfCourse(courseId);
    //        return ApiResponse.<Boolean>builder()
    //                .result(wasInstructor)
    //                .message(wasInstructor ? "Người dùng từng là giảng viên của khóa học này" : "Người dùng chưa từng là giảng viên của khóa học này")
    //                .build();
    //    }

}
