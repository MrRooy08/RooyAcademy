package com.test.permissionusesjwt.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Course {
    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    String id;

    @Column(name = "name", unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String name;

    int totalTime;

    @Column(columnDefinition = "TEXT")
    String description;
    BigDecimal price;
    String imageUrl;
    String videoUrl;
    


    @ManyToOne
    Level levelCourse;


//    @OneToMany (mappedBy = "course", cascade = CascadeType.ALL)
//    Set<Review> reviews;

    //fetch eager tải du lieu ngay lap tuc
    @OneToMany (mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    Set<Lesson> lessons;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    @ManyToMany (mappedBy = "courses")
    Set<Category> category;



    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
