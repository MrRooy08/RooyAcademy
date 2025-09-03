package com.test.permissionusesjwt.dto.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.test.permissionusesjwt.dto.request.EnrolledCourseDto;
import com.test.permissionusesjwt.entity.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import com.test.permissionusesjwt.dto.response.CartResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults (level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse {
    String username;
    String firstName;
    String lastName;
    LocalDate dob;
    Set<RoleResponse> roles;

    String instructorId;
    String studentId;
    CartResponse cart;

    @Builder.Default
    List<EnrolledCourseDto> enrolledCourses = new ArrayList<>();
}
