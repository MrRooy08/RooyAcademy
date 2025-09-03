package com.test.permissionusesjwt.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table (name = "danh_muc")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column (name = "ma_danh_muc")
    String id;

    @Column(name = "ten_danh_muc", unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String name;

    @Column(name = "mo_ta")
    String description;

    @ManyToOne
    @JoinColumn( name = "ma_danh_muc_cha")
    @Nullable
    Category parent;

    @OneToMany( mappedBy = "parent" , cascade = CascadeType.ALL)
    List<Category> subCategories;

    @OneToMany (mappedBy = "category", cascade = CascadeType.ALL)
    Set<Course> courses;

    @OneToMany (mappedBy = "category", cascade = CascadeType.ALL)
    Set<Topic> topics;

}
