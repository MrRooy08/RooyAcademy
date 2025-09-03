package com.test.permissionusesjwt.repository;

import com.test.permissionusesjwt.entity.Enrollment;
import com.test.permissionusesjwt.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, String> {
    List<Enrollment> findByProfile(Profile profile);
    
    @Query("SELECT e FROM Enrollment e WHERE e.course.id = :courseId")
    List<Enrollment> findByCourseId(@Param("courseId") String courseId);
    
    @Query("SELECT e FROM Enrollment e WHERE e.course.id = :courseId AND e.profile.user.id = :userId")
    Optional<Enrollment> findByCourseIdAndUserId(@Param("courseId") String courseId, @Param("userId") String userId);
}
