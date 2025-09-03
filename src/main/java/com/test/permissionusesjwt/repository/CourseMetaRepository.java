package com.test.permissionusesjwt.repository;

import com.test.permissionusesjwt.entity.CourseMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseMetaRepository extends JpaRepository<CourseMeta, String> {

    Optional<List<CourseMeta>> findAllByCourseId(String courseId);
}
