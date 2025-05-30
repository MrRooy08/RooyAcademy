package com.test.permissionusesjwt.repository;

import com.test.permissionusesjwt.entity.Course;
import com.test.permissionusesjwt.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, String> {
    Optional<Lesson> findLessonByName(String name);
    List<Lesson> findByCourseId (String id);
}
