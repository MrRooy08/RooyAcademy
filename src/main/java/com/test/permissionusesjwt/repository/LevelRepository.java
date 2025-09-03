package com.test.permissionusesjwt.repository;

import com.test.permissionusesjwt.entity.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LevelRepository  extends JpaRepository<Level, String> {
    Optional<Level> findLevelByName(String name);
    Optional<Level> findLevelById(String id);
}
