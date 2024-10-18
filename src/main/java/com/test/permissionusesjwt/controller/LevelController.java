package com.test.permissionusesjwt.controller;

import com.test.permissionusesjwt.dto.request.ApiResponse;
import com.test.permissionusesjwt.dto.request.LevelRequest;
import com.test.permissionusesjwt.dto.response.LevelResponse;
import com.test.permissionusesjwt.service.LevelService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/level")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class LevelController {

    LevelService levelService;

    @PostMapping("/create-level")
    ApiResponse<LevelResponse> createLevel(@RequestBody LevelRequest levelRequest) {
            return ApiResponse.<LevelResponse>builder()
                    .result(levelService.createLevel(levelRequest))
                    .build();
    }

    @PutMapping("/{name}")
    ApiResponse<LevelResponse> updateLevel(@PathVariable String name,@RequestBody LevelRequest levelRequest) {
            return ApiResponse.<LevelResponse>builder()
                    .result(levelService.updateLevel(name, levelRequest))
                    .build();
    }

    @DeleteMapping("/{name}")
    ApiResponse<Void> deleteLevelByName (@PathVariable String name) {
        levelService.deleteByName(name);
        return ApiResponse.<Void>builder()
                .build();
    }

    @GetMapping()
    ApiResponse<List<LevelResponse>> getAllLevels() {
        return ApiResponse.<List<LevelResponse>>builder()
                .result(levelService.getAllLevels())
                .build();
    }
}
