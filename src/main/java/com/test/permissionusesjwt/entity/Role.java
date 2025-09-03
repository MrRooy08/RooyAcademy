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
@Table (name = "vai_tro")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ma_vai_tro")
    String id;


    @Column(name = "ten_quyen", unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String name;

    @Column(name = "mo_ta")
    String description;

    @ManyToMany (mappedBy = "roles")
    Set<User> users;



    @ManyToMany
    @JoinTable(
            name = "vai_tro_quyen",
            joinColumns = @JoinColumn (name = "ma_vai_tro"),
            inverseJoinColumns = @JoinColumn (name = "ma_quyen")
    )
    Set<Permission> permissions;

    public Role(String name) {
        this.name = name;
    }
}
