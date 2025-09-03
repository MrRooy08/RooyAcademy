package com.test.permissionusesjwt.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "bai_giang_hoan_thanh")
@IdClass(CompletedLessonId.class)
public class CompletedLesson {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_tien_do")
    Progress progress;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_bai_giang")
    Lesson lesson;

    @Column(name = "thoi_gian_hoan_thanh")
    LocalDateTime finishedAt;

    @PrePersist
    void prePersist() {
        finishedAt = LocalDateTime.now();
    }
}
