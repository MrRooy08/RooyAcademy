package com.test.permissionusesjwt.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileSocialID implements Serializable {

    @Column (name = "profile_id")
    String profile_id;

    @Column (name = "social_id")
    String social_id;

}
