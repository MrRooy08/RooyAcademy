package com.test.permissionusesjwt.controller;

import com.test.permissionusesjwt.dto.request.ApiResponse;
import com.test.permissionusesjwt.dto.request.CartCreateRequest;
import com.test.permissionusesjwt.dto.request.CartItemRequest;
import com.test.permissionusesjwt.dto.response.CartResponse;
import com.test.permissionusesjwt.service.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CartController {

    CartService cartService;

    @PostMapping()
    public ApiResponse<CartResponse> addItem(@RequestBody CartCreateRequest request) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.create(request))
                .build();
    }



    @DeleteMapping("/{id}/items/{courseId}")
    public ApiResponse<CartResponse> removeItem(@PathVariable("id") String id, @PathVariable("courseId") String courseId) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.removeItem(id, courseId))
                .build();
    }

}
