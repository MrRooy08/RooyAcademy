package com.test.permissionusesjwt.dto.request;

import com.test.permissionusesjwt.validator.DobConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults (level = AccessLevel.PRIVATE)
public class UserCreationRequest {

    @Size(min = 3, message = "USERNAME_INVALID")
    @NotBlank
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]{6,}@gmail\\.com$",
            message = "Email phải có định dạng Gmail và ít nhất 6 ký tự trước @"
    )
    String username;

    @Size(min = 8, message = "PASSWORD_INVALID")
    @NotBlank
    String password;
    String otp;
    Set<String> roles;

}
