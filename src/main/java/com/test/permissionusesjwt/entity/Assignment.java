package com.test.permissionusesjwt.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "bai_tap")
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ma_bai_tap")
    String id;

    @Column(name = "ten_bai_tap", columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String name;

    @Column(name = "mo_ta")
    String description;

    @Column(name = "huong_dan", columnDefinition = "TEXT")
    String instructions;

    @Column(name = "thu_tu")
    int index;

    @Column(name = "thoi_gian_du_kien")
    Integer estimatedTime; // Thời gian dự kiến hoàn thành (phút)

    @CreationTimestamp
    @Column(updatable = false, name = "ngay_tao")
    Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    Timestamp updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_phan_hoc")
    Section section;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_giang_vien")
    Instructor instructor; // Giảng viên tạo bài tập

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<AssignmentQuestion> questions;
} 