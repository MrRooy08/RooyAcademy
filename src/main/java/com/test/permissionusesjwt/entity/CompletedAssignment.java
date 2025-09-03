package com.test.permissionusesjwt.entity;

import com.test.permissionusesjwt.enums.FinishStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "bai_tap_hoan_thanh", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"ma_tien_do", "ma_bai_tap"})
       })
public class CompletedAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ma_bai_hoan_thanh")
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_tien_do")
    Progress progress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_bai_tap")
    Assignment assignment;

    @OneToMany(mappedBy = "completedAssignment", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    List<AnswerStudent> answerStudent = new ArrayList<>();

    @Column(name = "thoi_gian_hoan_thanh")
    LocalDateTime finishedAt;

    @Column(name = "nhan_xet", columnDefinition = "TEXT")
    String feedback; // Nhận xét từ giảng viên

    @Column(name = "trang_thai")
    @Enumerated(EnumType.STRING)
    FinishStatus status = FinishStatus.UNFINISHED; // SUBMITTED, EVALUATED, etc.

    @CreationTimestamp
    @Column(updatable = false, name = "ngay_tao")
    Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    Timestamp updatedAt;

    @PrePersist
    void prePersist() {
        if (finishedAt == null) {
            finishedAt = LocalDateTime.now();
        }
    }
} 