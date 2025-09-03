package com.test.permissionusesjwt.controller;

import com.test.permissionusesjwt.dto.request.ApiResponse;
import com.test.permissionusesjwt.dto.request.EmailRequest;
import com.test.permissionusesjwt.service.EmailService;
import com.test.permissionusesjwt.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmailController {
    EmailService emailService;

    @PostMapping("/send-dashboard-link")
    public ApiResponse<Void> sendDashboardLink(@RequestBody EmailRequest request) {
        emailService.sendDashboardLinkEmail(request.getTo(), request.getSubject());
        return ApiResponse.<Void>builder()
                .message("Gửi email thành công")
                .build();
    }

}
