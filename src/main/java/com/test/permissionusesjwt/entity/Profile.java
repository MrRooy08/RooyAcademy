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
@Table (name = "ho_so_hoc_vien")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ma_hoc_vien")
    String id;

    @OneToOne
    @JoinColumn (name = "ma_nguoi_dung", nullable = false)
    User user;

    @Column (name = "ho_lot",nullable = false)
    String firstName;
    @Column (name = "ten",nullable = false)
    String lastName;

    @Column (name = "tin_noi_bat",nullable = false)
    String headline;

    @Column(name = "gioi_thieu", columnDefinition = "TEXT",nullable = false)
    String bio;

    @OneToMany (mappedBy = "profile", fetch = FetchType.EAGER)
    Set<ProfileSocial> profile;

    @Column (name = "nam_sinh",nullable = false)
    LocalDate dob;
}
