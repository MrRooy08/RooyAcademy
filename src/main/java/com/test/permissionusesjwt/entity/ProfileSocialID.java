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

    @Column (name = "ma_hoc_vien")
    String profile_id;

    @Column (name = "ma_mxh")
    String social_id;

}
