package com.test.permissionusesjwt.controller;

import com.test.permissionusesjwt.dto.request.InvitationRequest;
import com.test.permissionusesjwt.dto.response.AddInstructorResponse;
import com.test.permissionusesjwt.dto.response.InstructorCourseResponse;
import com.test.permissionusesjwt.service.CourseService;
import java.util.List;
import com.test.permissionusesjwt.dto.request.ApiResponse;
import com.test.permissionusesjwt.dto.response.InvitationResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invitation")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class InvitationController {
    CourseService courseService;


    @GetMapping("/myList")
    ApiResponse<List<AddInstructorResponse>> listMyInvitations() {
        return ApiResponse.<List<AddInstructorResponse>>builder()
                .result(courseService.getMyInvitations())
                .build();
    }


    @PostMapping("/response")
    ApiResponse<InvitationResponse> invitationResponse(@RequestBody InvitationRequest request) {
        return ApiResponse.<InvitationResponse>builder()
                .result(courseService.respondToCoInstructorInvite(request.getCourseId(), request.isStatus()))
                .build();
    }
}
