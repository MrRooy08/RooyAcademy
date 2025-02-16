package com.test.permissionusesjwt.controller;


import com.test.permissionusesjwt.dto.request.ApiResponse;
import com.test.permissionusesjwt.dto.request.EnrollmentRequest;
import com.test.permissionusesjwt.dto.response.CourseResponse;
import com.test.permissionusesjwt.dto.response.EnrollmentResponse;
import com.test.permissionusesjwt.entity.Enrollment;
import com.test.permissionusesjwt.service.EnrollmentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/enrollment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class EnrollmentController {

    EnrollmentService enrollmentService;

    @PostMapping()
    public ApiResponse<EnrollmentResponse> create (@RequestBody EnrollmentRequest request)
    {
        return ApiResponse.<EnrollmentResponse>builder()
                .result(enrollmentService.create(request))
                .build();
    }

}
