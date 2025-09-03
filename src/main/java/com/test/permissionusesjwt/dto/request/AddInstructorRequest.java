package com.test.permissionusesjwt.dto.request;


import com.test.permissionusesjwt.entity.Category;
import com.test.permissionusesjwt.entity.Permission;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddInstructorRequest {
    String instructor;
    @Builder.Default
    Set<String> permissions = new HashSet<>();
}
