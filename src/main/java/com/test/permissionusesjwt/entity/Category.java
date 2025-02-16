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
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "name", unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String name;

    @ManyToOne
    @JoinColumn( name = "parent_id")
    @Nullable
    Category parent;

    @OneToMany( mappedBy = "parent" , cascade = CascadeType.ALL)
    List<Category> subCategories;

    @ManyToMany
            @JoinTable(
                    name = "category_course",
                    joinColumns = @JoinColumn (name = "category_id"),
                    inverseJoinColumns = @JoinColumn (name = "course_id")
            )
    Set<Course> courses;

}
