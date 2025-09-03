package com.test.permissionusesjwt.entity;

import com.test.permissionusesjwt.enums.ActiveStatus;
import com.test.permissionusesjwt.enums.InviteStatus;
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
@Table(name = "giang_vien_khoa_hoc",
uniqueConstraints = @UniqueConstraint(columnNames = {"ma_khoa_hoc","ma_giang_vien"}))
public class InstructorCourse {
    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    @Column (name = "ma_quan_ly")
    String id;

    @ManyToOne
    @JoinColumn (name = "ma_khoa_hoc")
    Course course;

    @ManyToOne
    @JoinColumn (name = "ma_giang_vien")
    Instructor instructor;

    @Column (name = "la_chu_khoa_hoc")
    boolean isOwner;

    @Column (name = "ngay_tham_gia")
    LocalDateTime createdAt;

    @Column (name = "ngay_moi")
    LocalDateTime invitedAt;

    @Enumerated (EnumType.STRING)
    @Column (name = "con_hoat_dong")
    ActiveStatus isActive;

    @Builder.Default
    @ManyToMany
    @JoinTable (
            name = "quyen_giang_vien_khoa_hoc",
            joinColumns = @JoinColumn (name = "ma_quan_ly"),
            inverseJoinColumns = @JoinColumn (name = "ma_quyen")
    )
    Set<Permission> permissions = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "moi_tham_gia")
    InviteStatus status;

    @ElementCollection (fetch = FetchType.EAGER)
    @CollectionTable(name = "pending_quyen_giang_vien", joinColumns = @JoinColumn(name = "ma_quan_ly"))
    @Column(name = "ma_quyen")
    Set<String> pendingPermissionIds = new HashSet<>();


    @PrePersist
    protected void onCreate() {
        invitedAt = LocalDateTime.now();
    }


}
