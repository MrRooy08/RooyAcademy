package com.test.permissionusesjwt.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "chi_tiet_don_hang")
@IdClass(OrderDetailId.class)
public class OrderDetail {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_don_hang")
    Order order;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_khoa_hoc")
    Course course;

    @Column(name = "gia_goc")
    BigDecimal originalPrice;

    @Column(name = "gia_sau_giam")
    BigDecimal discountedPrice;

    @Column(name = "ma_giam_gia")
    String discountCode;

    @Column(name = "ngay_tao")
    LocalDateTime createdAt;

    @Column(name = "ngay_cap_nhat")
    LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
