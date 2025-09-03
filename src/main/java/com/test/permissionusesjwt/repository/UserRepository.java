package com.test.permissionusesjwt.repository;

import com.test.permissionusesjwt.entity.Instructor;
import com.test.permissionusesjwt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username);

    Optional<User>  findByUsername(String username);

    @Query ("select i from User u JOIN u.instructor i where u.username = :username and u.id = i.user.id")
    Optional<Instructor> findInstructorByUsername (@Param("username") String username);
}
