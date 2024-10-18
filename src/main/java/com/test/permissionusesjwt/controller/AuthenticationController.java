package com.test.permissionusesjwt.controller;

import com.nimbusds.jose.JOSEException;
import com.test.permissionusesjwt.dto.request.*;
import com.test.permissionusesjwt.dto.response.AuthenticationResponse;
import com.test.permissionusesjwt.dto.response.IntrospectResponse;
import com.test.permissionusesjwt.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor //tự động tạo hàm tạo autowire
@FieldDefaults (level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/log-in")
    ApiResponse<AuthenticationResponse> authenticate (@RequestBody AuthenticationRequest request)
    {
        var result  =  authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticateIntrospect (@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        System.out.println("Received Token: " + request.getToken());

        var result  =  authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> authenticate (@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        var result  =  authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/log-out")
    ApiResponse<Void> logOut (@RequestBody LogOutRequest request)
            throws ParseException, JOSEException {
        authenticationService.logOut(request);
        return ApiResponse.<Void>builder()
                .build();
    }
}
