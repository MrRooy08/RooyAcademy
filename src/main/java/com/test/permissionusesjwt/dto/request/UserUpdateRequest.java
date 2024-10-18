package com.test.permissionusesjwt.dto.request;

import com.test.permissionusesjwt.validator.DobConstraint;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    String password;
    String firstName;
    String lastName;
    @DobConstraint(min = 7, message = "INVALID_DOB")
    LocalDate dob;
    List<String> roles;

}
