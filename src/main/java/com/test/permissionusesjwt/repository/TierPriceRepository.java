package com.test.permissionusesjwt.repository;

import com.test.permissionusesjwt.entity.TierPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TierPriceRepository extends JpaRepository<TierPrice, String> {
}
