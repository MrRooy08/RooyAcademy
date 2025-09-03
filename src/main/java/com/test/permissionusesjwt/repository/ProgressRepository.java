package com.test.permissionusesjwt.repository;

import com.test.permissionusesjwt.entity.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, String> {
    boolean existsByEnrollmentId(String enrollmentId);
    Optional<Progress> findByEnrollmentId(String enrollmentId);
}
