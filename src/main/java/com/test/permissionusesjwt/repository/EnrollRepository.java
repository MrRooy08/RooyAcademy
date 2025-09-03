package com.test.permissionusesjwt.repository;

import com.test.permissionusesjwt.entity.Course;
import com.test.permissionusesjwt.entity.Enrollment;
import com.test.permissionusesjwt.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollRepository extends JpaRepository<Enrollment,String> {

    boolean existsByProfileAndCourse(Profile profile, Course course);

    // Retrieve all enrollments for a given profile
    java.util.List<Enrollment> findByProfile(Profile profile);
}
