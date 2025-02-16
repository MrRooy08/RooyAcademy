package com.test.permissionusesjwt.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @OneToOne
    @JoinColumn (name = "user_id", nullable = false)
    User user;

    String firstName;
    String lastName;
    String headline;

    @Column(columnDefinition = "TEXT")
    String bio;

    @OneToMany (mappedBy = "profile", fetch = FetchType.EAGER)
    Set<ProfileSocial> profile;


    LocalDate dob;
}
