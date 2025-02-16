package com.test.permissionusesjwt.dto.response;

import com.test.permissionusesjwt.dto.request.ProfileSocialRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileResponse {
    String user_id;
    String firstName;
    String lastName;
    String headline;
    String bio;
    LocalDate dob;

    Set<ProfileSocialResponse> social;
}
