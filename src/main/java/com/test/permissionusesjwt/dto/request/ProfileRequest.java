package com.test.permissionusesjwt.dto.request;

import com.test.permissionusesjwt.validator.DobConstraint;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileRequest {
    String user_id;
    String firstName;
    String lastName;
    String headline;
    String bio;

    @DobConstraint(min = 7, message = "INVALID_DOB")
    LocalDate dob;


    Set<ProfileSocialRequest> social;

}
