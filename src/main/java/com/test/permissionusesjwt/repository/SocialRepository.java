package com.test.permissionusesjwt.repository;

import com.test.permissionusesjwt.entity.Role;
import com.test.permissionusesjwt.entity.Social;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocialRepository  extends JpaRepository<Social,String> {

    Optional<Social> findByUrl(String url);
}
