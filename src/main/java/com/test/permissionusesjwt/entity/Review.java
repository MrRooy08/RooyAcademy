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
public class Review {
    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    String id;
    int rating;
    String comments;
    LocalDateTime createdAt;

    String userId;
    String courseId;

//    @ManyToOne
//    @JoinColumn(name = "course_id", nullable = false)
//    Course course;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
