package com.test.permissionusesjwt.entity;

import com.test.permissionusesjwt.enums.ApproveStatus;
import com.test.permissionusesjwt.enums.CourseStatus;
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
@Table (name = "khoa_hoc")
public class Course {
    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    @Column (name = "ma_khoa_hoc")
    String id;

    @ManyToOne
    @JoinColumn (name = "ma_danh_muc")
    Category category;

    @ManyToOne
    @JoinColumn (name = "ma_chu_de")
    Topic topic;

    @ManyToOne
    @JoinColumn (name = "ma_trinh_do")
    Level levelCourse;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "ma_gia_goc", nullable = true)
    TierPrice price;


    @Column(name = "ten_khoa_hoc", columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String name;

    @Column(name = "mo_ta",columnDefinition = "TEXT")
    String description;

    @Column(name = "phu_de",columnDefinition = "TEXT")
    String subtitle;

    @Column (name = "hinh_anh")
    String imageUrl;

    @Column (name = "video_url")
    String videoUrl;

//    String videoUrl;
//    @OneToMany (mappedBy = "course", cascade = CascadeType.ALL)
//    Set<Review> reviews;

    @Column (name = "ngay_tao")
    LocalDateTime createdAt;

    @Column (name = "ngay_cap_nhat")
    LocalDateTime updatedAt;

    @Enumerated (EnumType.STRING)
    @Column (name = "trang_thai_khoa_hoc")
    CourseStatus isActive;

    @Enumerated (EnumType.STRING)
    @Column (name = "trang_thai_duyet")
    ApproveStatus approveStatus;

    @Enumerated (EnumType.STRING)
    @Column (name = "pham_vi")
    CourseStatus rangeCourse =CourseStatus.PUBLIC;


    //fetch eager tải du lieu ngay lap tuc
    @OneToMany (mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    Set<Section> sections;

    @ManyToOne
    @JoinColumn (name =  "nguoi_duyet")
    User user;

    @Column (name = "ngay_duyet")
    LocalDateTime approvedAt;

    @Column (name = "tham_gia_uu_dai")
    Boolean followCoupon;

    @Builder.Default
    @OneToMany (mappedBy = "course",fetch = FetchType.EAGER, orphanRemoval = true)
    Set<CourseMeta> courseMeta = new HashSet<>();

    @Builder.Default
    @OneToMany (mappedBy = "course",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    Set<InstructorCourse> instructorCourse = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
