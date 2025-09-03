package com.test.permissionusesjwt.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class ProfileSocial {


    @EmbeddedId
    ProfileSocialID profileSocialID;

    @ManyToOne
    @MapsId ("profile_id")
    @JoinColumn (name = "ma_hoc_vien")
    Profile profile;

    @ManyToOne
    @MapsId ("social_id")
    @JoinColumn (name = "ma_mxh")
    Social social;
}
