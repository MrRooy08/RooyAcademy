package com.test.permissionusesjwt.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.test.permissionusesjwt.enums.OrderStatus;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "don_hang")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ma_don_hang")
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nguoi_dung")
    User user;

    @Column(name = "tong_tien")
    BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    OrderStatus status;

    @Column(name = "phuong_thuc_thanh_toan")
    String paymentMethod;

    @Column(name = "ngay_tao")
    LocalDateTime createdAt;

    @Column(name = "ngay_cap_nhat")
    LocalDateTime updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    Set<OrderDetail> items = new HashSet<>();

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
