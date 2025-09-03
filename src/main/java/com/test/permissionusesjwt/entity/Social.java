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
@Table (name = "mang_xa_hoi")
public class Social {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column (name = "ma_mxh")
    String id;

    @Column(name="url",unique=true,columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci", nullable=false)
    String url;

    @Column (name = "ten_mxh")
    String name;

    @OneToMany (mappedBy = "social", fetch = FetchType.LAZY)
    Set<ProfileSocial> profile;
}
