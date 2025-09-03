package com.test.permissionusesjwt.repository;

import com.test.permissionusesjwt.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaRepository extends JpaRepository<Media, String> {
}
