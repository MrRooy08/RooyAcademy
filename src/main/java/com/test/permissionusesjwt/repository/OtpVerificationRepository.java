package com.test.permissionusesjwt.repository;

import com.test.permissionusesjwt.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {
    Optional<OtpVerification> findTopByEmailOrderByExpirationTimeDesc(String email);

    void deleteByExpirationTimeBefore(LocalDateTime now);
}
