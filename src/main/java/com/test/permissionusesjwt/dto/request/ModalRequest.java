package com.test.permissionusesjwt.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ModalRequest {
    List<QuestionUpdateRequest> add;
    List<QuestionUpdateRequest> update;
    List<String> delete;
}
