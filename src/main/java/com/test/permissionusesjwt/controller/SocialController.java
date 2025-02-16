package com.test.permissionusesjwt.controller;


import com.test.permissionusesjwt.dto.request.ApiResponse;
import com.test.permissionusesjwt.dto.request.SocialRequest;
import com.test.permissionusesjwt.dto.response.SocialResponse;
import com.test.permissionusesjwt.service.SocialService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/social")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class SocialController {
    SocialService socialService;

    @PostMapping("/create-social")
    public ApiResponse<SocialResponse> createSocial(@RequestBody SocialRequest socialRequest) {
        return ApiResponse.<SocialResponse>builder()
                .result(socialService.createSocial(socialRequest))
                .build();
    }

}
