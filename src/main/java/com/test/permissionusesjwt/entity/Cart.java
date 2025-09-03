package com.test.permissionusesjwt.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import jakarta.persistence.FetchType;
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
@Table(name = "gio_hang")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ma_gio_hang")
    String id;

    @OneToOne
    @JoinColumn(name = "ma_nguoi_dung", unique = true)
    User user;

    @Column(name = "tong_tien")
    BigDecimal totalAmount;

    @Column(name = "phuong_thuc_thanh_toan")
    String paymentMethod;

    @Column(name = "ngay_tao")
    LocalDateTime createdAt;

    @Column(name = "ngay_cap_nhat")
    LocalDateTime updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    Set<CartItem> items = new HashSet<>();

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
