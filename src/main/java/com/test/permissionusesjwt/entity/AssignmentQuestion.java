package com.test.permissionusesjwt.entity;

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
@Table(name = "cau_hoi_bai_tap")
public class AssignmentQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ma_cau_hoi")
    String id;

    @Column(name = "noi_dung_cau_hoi", columnDefinition = "TEXT")
    String questionContent;

    @Column(name = "thu_tu")
    int index;

    @CreationTimestamp
    @Column(updatable = false, name = "ngay_tao")
    Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    Timestamp updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_bai_tap")
    Assignment assignment;

    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    Answer answer;
} 