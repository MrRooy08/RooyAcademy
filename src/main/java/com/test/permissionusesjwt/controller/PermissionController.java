package com.test.permissionusesjwt.controller;

import com.test.permissionusesjwt.dto.request.ApiResponse;
import com.test.permissionusesjwt.dto.request.PermissionRequest;
import com.test.permissionusesjwt.dto.response.PermissionResponse;
import com.test.permissionusesjwt.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class PermissionController {

    PermissionService permissionService;

    @PostMapping
    ApiResponse<PermissionResponse> create (@RequestBody PermissionRequest request) {
            return ApiResponse.<PermissionResponse>builder()
                    .result(permissionService.createPermission(request))
                    .build();
    }

    @GetMapping
    ApiResponse<List<PermissionResponse>> getAll () {
        return ApiResponse.<List<PermissionResponse>>builder()
                .result(permissionService.getAll())
                .build();
    }

    @DeleteMapping("/{name}")
    ApiResponse<Void> delete (@PathVariable String name) {
        permissionService.delete(name);
        return ApiResponse.<Void>builder().build();
    }

}
