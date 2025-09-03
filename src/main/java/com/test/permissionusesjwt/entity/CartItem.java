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
@Table(name = "chi_tiet_gio_hang")
@IdClass(CartItemId.class)
public class CartItem {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_gio_hang")
    Cart cart;

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

    @Column(name = "ngay_them")
    LocalDateTime addedAt;

    @Column(name = "ngay_cap_nhat")
    LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        addedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
