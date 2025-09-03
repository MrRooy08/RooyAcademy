package com.test.permissionusesjwt.entity;

import com.test.permissionusesjwt.enums.FinishStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "dap_an_hoc_vien")
public class AnswerStudent {

    @EmbeddedId
    AnswerStudentId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("completedAssignmentId")
    @JoinColumn(name = "ma_bai_hoan_thanh")
    CompletedAssignment completedAssignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("questionId")
    @JoinColumn(name = "ma_cau_hoi")
    AssignmentQuestion question;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    FinishStatus finishStatus = FinishStatus.UNFINISHED;

    @Column(name = "noi_dung_tra_loi")
    String answerContent;

    @Column(name = "thoi_gian_hoan_thanh")
    Integer finishedTime; // Thời gian dự kiến hoàn thành (phút)

    @CreationTimestamp
    @Column(updatable = false, name = "ngay_hoan_thanh")
    Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    Timestamp updatedAt;

}
