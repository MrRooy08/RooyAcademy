package com.test.permissionusesjwt.entity;


import com.test.permissionusesjwt.enums.FinishStatus;
import com.test.permissionusesjwt.entity.Progress;
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
@Table (name = "tham_gia_khoa_hoc")
public class Enrollment {

    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    @Column (name = "ma_dang_ky")
    String id;

    @ManyToOne
    @JoinColumn(name = "ma_khoa_hoc")
    Course course;

    @ManyToOne
    @JoinColumn(name = "ma_hoc_vien")
    Profile profile;


    @Column (name = "ngay_dang_ky")
    LocalDateTime enrolled_at;

    @Column (name = "trang_thai_hoan_thanh")
    @Enumerated(EnumType.STRING)
    FinishStatus isFinished;

    @Column (name = "ngay_hoan_thanh")
    LocalDateTime date_finished;

    @Builder.Default
    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    Set<Progress> progresses = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        enrolled_at = LocalDateTime.now();

    }
}
