package com.test.permissionusesjwt.controller;

import com.test.permissionusesjwt.dto.request.ApiResponse;
import com.test.permissionusesjwt.dto.request.ProfileRequest;
import com.test.permissionusesjwt.dto.response.ProfileResponse;
import com.test.permissionusesjwt.service.ProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class ProfileController {
    ProfileService profileService;

    @PostMapping("/create-profile")
    public ApiResponse<ProfileResponse> createProfile (@RequestBody ProfileRequest profileRequest) {
        return ApiResponse.<ProfileResponse>builder()
                .result(profileService.createProfile(profileRequest))
                .build();
    }

    @PutMapping ("/save-profile")
    public ApiResponse<ProfileResponse> saveProfile (@RequestBody ProfileRequest profileRequest) {
        return ApiResponse.<ProfileResponse>builder()
                .result(profileService.save(profileRequest))
                .build();
    }

}
