package com.test.permissionusesjwt.repository;

import com.test.permissionusesjwt.entity.Category;
import com.test.permissionusesjwt.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, String> {
    Optional<Topic> findByName(String name);
}
