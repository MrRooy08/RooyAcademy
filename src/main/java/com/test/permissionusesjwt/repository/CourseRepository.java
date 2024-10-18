package com.test.permissionusesjwt.repository;

import com.test.permissionusesjwt.entity.Course;
import com.test.permissionusesjwt.entity.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    Optional<Course> findByName(String name);
}
