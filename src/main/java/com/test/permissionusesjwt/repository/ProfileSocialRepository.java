package com.test.permissionusesjwt.repository;

import com.test.permissionusesjwt.entity.ProfileSocial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileSocialRepository  extends JpaRepository<ProfileSocial, String> {
}
