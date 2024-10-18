package com.test.permissionusesjwt.dto.response;

import com.test.permissionusesjwt.entity.Level;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LevelResponse {
    String name;
    String description;
}
