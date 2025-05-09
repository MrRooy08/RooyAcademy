package com.test.permissionusesjwt.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults (level = AccessLevel.PRIVATE)
public class PermissionResponse {

    String name;
    String description;
}
