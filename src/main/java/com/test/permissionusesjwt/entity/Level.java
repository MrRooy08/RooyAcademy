package com.test.permissionusesjwt.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table (name = "trinh_do")
public class Level {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column (name = "ma_trinh_do")
    String id;

    @Column(name = "ten_trinh_do", unique = true, columnDefinition = "VARCHAR(50) COLLATE utf8mb4_unicode_ci")
    String name;

    @Column (name = "mo_ta")
    String description;

    @OneToMany (mappedBy = "levelCourse")
    Set<Course> courses;

}
