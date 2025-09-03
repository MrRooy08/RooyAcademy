package com.test.permissionusesjwt.controller;

import com.test.permissionusesjwt.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/instructor")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InstructorController {

    UserService userService;

    @PostMapping("/grant")
    public ResponseEntity<String> grantInstructorRole() {
        userService.grantInstructorRole();
        return ResponseEntity.ok().body("Ban da co the giang dạy");
    }
} 