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
public class Social {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name="url",unique=true,columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci", nullable=false)
    String url;

    @OneToMany (mappedBy = "social", fetch = FetchType.LAZY)
    Set<ProfileSocial> profile;
}
