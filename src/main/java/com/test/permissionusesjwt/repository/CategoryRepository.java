package com.test.permissionusesjwt.repository;

import com.test.permissionusesjwt.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

     Optional<Category> findByName(String name);

     List<Category> findByParent(Category parent);

     List<Category> findByParentIsNull();


}
