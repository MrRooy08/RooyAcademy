package com.test.permissionusesjwt.repository;

import com.test.permissionusesjwt.entity.CartItem;
import com.test.permissionusesjwt.entity.CartItemId;
import com.test.permissionusesjwt.entity.Cart;
import com.test.permissionusesjwt.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, CartItemId> {
    boolean existsByCartAndCourse(Cart cart, Course course);
    Optional<CartItem> findByCartAndCourse(Cart cart, Course course);
    void deleteByCartAndCourse(Cart cart, Course course);
}
