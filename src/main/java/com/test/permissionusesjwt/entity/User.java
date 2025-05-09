package com.test.permissionusesjwt.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class User {
    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    String id;

    @Column(name = "username", unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String username;
    String password;


    @ManyToMany
    Set<Role> roles;


    //orPhanRemoval sẽ xoá dữ liệu profile nếu k được mapping tới User
    @OneToOne (mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    Profile profile;

}
