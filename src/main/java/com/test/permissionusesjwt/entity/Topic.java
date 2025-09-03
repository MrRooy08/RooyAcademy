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
@Table(name = "chu_de_hoc")
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column (name = "ma_chu_de")
    String id;

    @Column(name = "ten_chu_de", unique = true, nullable = false, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String name;

    @Column(name = "mo_ta",columnDefinition = "TEXT")
    String description;

    @ManyToOne
    @JoinColumn(name = "ma_danh_muc")
    Category category;

    @OneToMany (mappedBy = "topic")
    Set<Course> courses;

}
