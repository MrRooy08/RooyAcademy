package com.test.permissionusesjwt.repository;

import com.test.permissionusesjwt.entity.Profile;
import com.test.permissionusesjwt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository <Profile, String> {
    Optional<Profile> findByUserId(String user_id);
}
