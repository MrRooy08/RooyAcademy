package com.test.permissionusesjwt.repository;

import com.test.permissionusesjwt.entity.Course;
import com.test.permissionusesjwt.entity.Enrollment;
import com.test.permissionusesjwt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollRepository extends JpaRepository<Enrollment,String> {

    boolean existsByUserIdAndCourseId(User userId, Course courseId);
}
