package com.test.permissionusesjwt.repository;

import com.test.permissionusesjwt.entity.Cart;
import com.test.permissionusesjwt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {
    Optional<Cart> findByUser(User user);
}
