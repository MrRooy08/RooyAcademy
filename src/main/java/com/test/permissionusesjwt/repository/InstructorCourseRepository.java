package com.test.permissionusesjwt.repository;

import com.test.permissionusesjwt.entity.Instructor;
import com.test.permissionusesjwt.entity.InstructorCourse;
import com.test.permissionusesjwt.enums.InviteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstructorCourseRepository extends JpaRepository<InstructorCourse, String> {
    boolean existsByCourseId(String courseId);
    Optional<InstructorCourse> findByCourseIdAndInstructor(String course_id, Instructor instructor);
    boolean existsByCourse_IdAndInstructorAndIsOwnerTrue(String courseId, Instructor instructor);

    @Query("SELECT ic FROM InstructorCourse ic " +
            "LEFT JOIN FETCH ic.instructor i " +
            "LEFT JOIN FETCH ic.permissions p " +
            "WHERE ic.course.id = :courseId " +
            "ORDER BY ic.createdAt ASC"
    )
    List<InstructorCourse> findInstructorsWithPermissionsByCourseId(@Param("courseId") String courseId);

    List<InstructorCourse> findByInstructor_IdAndStatus(String instructorId, InviteStatus status);
    boolean existsByInstructor_IdAndStatus(String instructorId, InviteStatus status);
    boolean existsByCourse_IdAndInstructorAndStatus(String courseId, Instructor instructor, InviteStatus status);

    @Query("SELECT COUNT(ic) > 0 FROM InstructorCourse ic " +
           "WHERE ic.course.id = :courseId " +
           "AND ic.instructor.user.id = :userId " +
           "AND ic.status = 'ACCEPTED' " +
           "AND ic.isActive = 'Inactive'")
    boolean hasUserBeenInstructorOfCourse(@Param("courseId") String courseId, @Param("userId") String userId);

}
