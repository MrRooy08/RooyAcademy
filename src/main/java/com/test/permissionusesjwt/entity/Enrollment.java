package com.test.permissionusesjwt.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Enrollment {

    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "courseId")
    Course courseId;

    @ManyToOne
    @JoinColumn(name = "userId")
    User userId;


    LocalDateTime enrolled_at;

    @PrePersist
    protected void onCreate() {
        enrolled_at = LocalDateTime.now();
    }
}
