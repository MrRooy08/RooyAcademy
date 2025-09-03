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
@Table(name = "dap_an_cau_hoi")
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ma_dap_an")
    String id;

    @Column(name = "noi_dung_dap_an", columnDefinition = "TEXT")
    String answerContent;

    @CreationTimestamp
    @Column(updatable = false, name = "ngay_tao")
    Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    Timestamp updatedAt;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_cau_hoi")
    AssignmentQuestion question;
} 