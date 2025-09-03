package com.test.permissionusesjwt.entity;

import com.test.permissionusesjwt.enums.FinishStatus;
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
@Table(name = "tien_do")
public class Progress {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ma_tien_do")
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_dang_ky")
    Enrollment enrollment;

    @Enumerated(EnumType.STRING)
    @Column(name = "da_hoan_thanh")
    @Builder.Default
    FinishStatus finishStatus = FinishStatus.UNFINISHED;

    @Column(name = "thoi_gian_hoan_thanh")
    LocalDateTime finishedAt;

    @Builder.Default
    @OneToMany(mappedBy = "progress", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    Set<CompletedLesson> completedLessons = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "progress", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    Set<CompletedAssignment> completedAssignments = new HashSet<>();

    public void markCompleted() {
        this.finishStatus = FinishStatus.FINISHED;
        this.finishedAt = LocalDateTime.now();
    }
}
