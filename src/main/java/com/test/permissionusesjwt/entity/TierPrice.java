package com.test.permissionusesjwt.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "muc_gia")
public class TierPrice {
    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    @Column (name = "ma_muc_gia")
    String id;

    @Column(name = "gia", precision = 10, scale = 2)
    BigDecimal price;

    @Column (name = "mo_ta", columnDefinition = "TEXT")
    String description;

    @OneToMany (mappedBy = "price")
    Set<Course> courses;
}
