package com.test.permissionusesjwt.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PagedResponse<T> {
    List<T> content;
    int pageNumber;
    int pageSize;
    long totalElements;
    int totalPages;
    boolean last;
}
