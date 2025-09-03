package com.test.permissionusesjwt.entity;

import com.test.permissionusesjwt.enums.TypeCourseMeta;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "thong_tin_phu_khoa_hoc")
public class CourseMeta {

    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    @Column (name = "id")
    String id;

    @ManyToOne
    @JoinColumn (name = "ma_khoa_hoc")
    Course course;

    @Enumerated (EnumType.STRING)
    @Column (name = "loai_thong_tin")
    TypeCourseMeta type;

    @Column (name = "noi_dung")
    String content;

    @Column (name = "thu_tu")
    int index;
}
