package com.test.permissionusesjwt.repository;

import com.test.permissionusesjwt.dto.response.PagedResponse;
import com.test.permissionusesjwt.entity.Course;
import com.test.permissionusesjwt.entity.Instructor;
import com.test.permissionusesjwt.entity.InstructorCourse;
import com.test.permissionusesjwt.enums.ApproveStatus;
import com.test.permissionusesjwt.enums.CourseStatus;
import com.test.permissionusesjwt.enums.InviteStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    Optional<Course> findByName(String name);

    Page<Course> findByInstructorCourse_InstructorAndIsActive (Instructor instructor, CourseStatus status, Pageable pageable);
    Page<Course> findByApproveStatus (ApproveStatus status, Pageable pageable);
    Page<Course> findByInstructorCourse_InstructorAndApproveStatus (Instructor instructor, ApproveStatus status, Pageable pageable);
    Page<Course> findByApproveStatusIn(List<ApproveStatus> approveStatuses, Pageable pageable);

    List<Course> findAllByApproveStatus (ApproveStatus status);

    Course findCourseBySectionsId(String sectionId);
    Optional<Course> findCourseById(String courseId);
}
