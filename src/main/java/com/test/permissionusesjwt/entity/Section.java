package com.test.permissionusesjwt.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table (name = "phan_hoc")
public class Section {
    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    @Column (name = "ma_phan_hoc")
    String id;

    @Column(name = "ten_phan_hoc", columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String name;

    @Column (name ="tieu_de")
    String title;

    @Column (name = "mo_ta")
    String description;

    @Column (name = "thu_tu")
    int index;

    @CreationTimestamp  //tu tao tgian
    @Column(updatable = false, name = "ngay_tao")  //khong bi thay doi khi co bat ky su kien gi xay ra
    Timestamp createdAt;

    @UpdateTimestamp //tu cap nhat tgian
    @Column(name = "ngay_cap_nhat")
    Timestamp updatedAt;

    @ManyToOne
    @JoinColumn(name = "ma_khoa_hoc", nullable = false)
    Course course;

    @Builder.Default
    @OneToMany (mappedBy = "section", fetch = FetchType.EAGER, orphanRemoval = true)
    Set<Lesson> lessons = new HashSet<>();

    @Builder.Default
    @OneToMany (mappedBy = "section", fetch = FetchType.EAGER, orphanRemoval = true)
    Set<Assignment> assignments = new HashSet<>();
}
