package com.test.permissionusesjwt.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "bai_giang")
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column (name = "ma_bai_giang")
    String id;

    @Column(name = "ten_phan_hoc", columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String name;

    @Column(columnDefinition = "TEXT", name = "noi_dung")
    String content;

    @Column (name = "mo_ta")
    String description;

    @Column (name = "video_url")
    String video_url;

    @Column (name = "thu_tu")
    int index;

    @Column (name = "xem_truoc")
    String isPreviewable;


    @CreationTimestamp  //tu tao tgian
    @Column(updatable = false, name = "ngay_tao")  //khong bi thay doi khi co bat ky su kien gi xay ra
    Timestamp createdAt;

    @UpdateTimestamp //tu cap nhat tgian
    @Column( name = "ngay_cap_nhat")
    Timestamp updatedAt;

    @ManyToOne
    @JoinColumn ( name = "ma_phan_hoc")
    Section section;

    @Builder.Default
    @OneToMany (mappedBy = "lesson", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    List<Media> mediaList = new ArrayList<>();


}
