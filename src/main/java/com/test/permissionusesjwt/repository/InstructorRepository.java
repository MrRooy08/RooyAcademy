package com.test.permissionusesjwt.repository;

import com.test.permissionusesjwt.entity.Course;
import com.test.permissionusesjwt.entity.Instructor;
import com.test.permissionusesjwt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, String> {
    Optional<Instructor> findByUser(User user);
}
