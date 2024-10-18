package com.test.permissionusesjwt.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Lesson {
    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    String id;

    @Column(name = "name", unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String name;

    String content;
    String videoUrl;

    @CreationTimestamp  //tu tao tgian
    @Column(updatable = false)  //khong bi thay doi khi co bat ky su kien gi xay ra
    Timestamp createdAt;

    @UpdateTimestamp //tu cap nhat tgian
    Timestamp updatedAt;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    Course course;
}
