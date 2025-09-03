package com.test.permissionusesjwt.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.nimbusds.jose.JOSEException;
import com.test.permissionusesjwt.dto.request.*;
import com.test.permissionusesjwt.dto.response.AuthenticationResponse;
import com.test.permissionusesjwt.dto.response.IntrospectResponse;
import com.test.permissionusesjwt.exception.AppException;
import com.test.permissionusesjwt.exception.ErrorCode;
import com.test.permissionusesjwt.service.AuthenticationService;
import com.test.permissionusesjwt.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.google.api.client.json.gson.GsonFactory;

import java.text.ParseException;
import java.util.Collections;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor //tự động tạo hàm tạo autowire
@FieldDefaults (level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    @NonFinal
    @Value("${google.client.id}")
    protected String googleClientId;
    AuthenticationService authenticationService;

    @PostMapping("/log-in")
    public ApiResponse<String> authenticate(@RequestBody AuthenticationRequest request,
                                            HttpServletResponse response) {
        AuthenticationResponse authResponse = authenticationService.authenticate(request);

        ResponseCookie cookie = ResponseCookie.from("access_token", authResponse.getToken())
                .httpOnly(true)
                .secure(true)  // cai nay dung cho https nha
                .path("/")
                .sameSite("None")
                .maxAge(3600)
                .build();

        // Set header Set-Cookie
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ApiResponse.<String>builder()
                .result("Login successfully")
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
    ApiResponse<Void> logOut (@CookieValue(name = "access_token") LogOutRequest request, HttpServletResponse response)
            throws ParseException, JOSEException {
        authenticationService.logOut(request);
        ResponseCookie cookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0) // Xoá cookie
                .sameSite("None")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
        return ApiResponse.<Void>builder()
                .build();
    }

    @PostMapping("/google")
    public ApiResponse<String> loginWithGoogle(@RequestBody GoogleLoginRequest request, HttpServletResponse response) {
        String idToken = request.getIdToken();

        GoogleIdToken.Payload payload = verifyGoogleIdToken(idToken);
        if (payload == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String email = payload.getEmail();
        String name = (String) payload.get("name");
        UserGoogleCreate userCreate = new UserGoogleCreate(email,name);

        AuthenticationResponse authResponse = authenticationService.authenticateGoogle(userCreate);

        ResponseCookie cookie = ResponseCookie.from("access_token", authResponse.getToken())
                .httpOnly(true)
                .secure(true)  // cai nay dung cho https nha
                .path("/")
                .sameSite("None")
                .maxAge(3600)
                .build();

        // Set header Set-Cookie
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ApiResponse.<String>builder()
                .code(200)
                .message("Đăng nhập Google thành công")
                .build();
    }

    private GoogleIdToken.Payload verifyGoogleIdToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();
            GoogleIdToken idToken = verifier.verify(idTokenString);
            return idToken != null ? idToken.getPayload() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
