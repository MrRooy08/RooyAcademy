package com.test.permissionusesjwt.repository;

import com.test.permissionusesjwt.entity.OrderDetail;
import com.test.permissionusesjwt.entity.OrderDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailId> {
}
