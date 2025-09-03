package com.test.permissionusesjwt.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.cglib.core.Local;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "nguoi_dung", indexes = {
        @Index(name = "idx_username", columnList = "ten_dang_nhap")
})
public class User {
    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    @Column(name ="ma_nguoi_dung")
    String id;

    @Column(name = "ten_dang_nhap", unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String username;
    @Column(name ="mat_khau")
    String password;

    @Column(name ="con_hoat_dong")
    Boolean isActive = true;

    @Column (name ="ngay_tao")
    LocalDateTime createdAt;

    @Column (name ="ngay_cap_nhat")
    LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "vai_tro_nguoi_dung_",
            joinColumns = @JoinColumn (name = "ma_nguoi_dung"),
            inverseJoinColumns = @JoinColumn (name = "ma_vai_tro")
    )
    Set<Role> roles = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    Cart cart;

    //orPhanRemoval sẽ xoá dữ liệu profile nếu k được mapping tới User
    @OneToOne (mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    Profile profile;

    @OneToOne (mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    Instructor instructor;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    List<Order> orders = new ArrayList<>();


}
