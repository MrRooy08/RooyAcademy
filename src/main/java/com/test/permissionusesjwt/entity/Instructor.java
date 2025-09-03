package com.test.permissionusesjwt.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "ho_so_giang_vien")
public class Instructor {
    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    @Column (name = "ma_ho_so")
    String id;

    @OneToOne
    @JoinColumn (name = "ma_nguoi_dung")
    User user;

    @Column (name = "ho_lot")
    String firstName;

    @Column (name = "ten_lot")
    String lastName;

    @Column (name = "kinh_nghiem")
    String experience;

    @Column (name = "bang_cap")
    String level;

    @Column (name = "tieu_su")
    String bio;

    @Column ( name = "ngay_tao")
    LocalDateTime createdAt;

    @Column (name = "ngay_cap_nhat")
    LocalDateTime updatedAt;

    @Builder.Default
    @OneToMany (mappedBy = "instructor", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<InstructorCourse> instructorCourse = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }


}
