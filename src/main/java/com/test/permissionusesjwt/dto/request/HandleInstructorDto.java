package com.test.permissionusesjwt.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HandleInstructorDto {
    @Builder.Default
    List<AddInstructorRequest> added = new ArrayList<>();
    @Builder.Default
    List<AddInstructorRequest> removed = new ArrayList<>();
    @Builder.Default
    List<AddInstructorRequest> permissionChanged = new ArrayList<>();
}
