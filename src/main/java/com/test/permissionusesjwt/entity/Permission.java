package com.test.permissionusesjwt.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table (name = "quyen")
public class Permission {
    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    @Column(name = "ma_quyen")
    String id;

    @Column(name = "ten_quyen", unique = true, columnDefinition = "VARCHAR(100) COLLATE utf8mb4_unicode_ci")
    String name;

    @Column(name = "mo_ta")
    String description;

    @ManyToMany (mappedBy = "permissions")
    Set<Role> roles;

    @ManyToMany (mappedBy = "permissions")
    Set<InstructorCourse> instructorCourses;
}
