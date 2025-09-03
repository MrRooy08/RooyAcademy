package com.test.permissionusesjwt.controller;

import com.test.permissionusesjwt.dto.request.ApiResponse;
import com.test.permissionusesjwt.dto.response.TierPriceResponse;
import com.test.permissionusesjwt.service.TierPriceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tier-price")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class TierPriceController {
    TierPriceService tierPriceService;

    @GetMapping()
    public ApiResponse<List<TierPriceResponse>> getAllPrice () {
        return ApiResponse.<List<TierPriceResponse>>builder()
                .result(tierPriceService.getAllPrice())
                .build();
    }
}
