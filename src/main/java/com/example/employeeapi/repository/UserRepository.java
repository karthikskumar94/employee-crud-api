package com.example.employeeapi.repository;

import com.example.employeeapi.entity.User;
import com.example.employeeapi.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Find all active users
     */
    List<User> findByActiveTrue();

    /**
     * Find users by role
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r = :role AND u.active = true")
    List<User> findByRolesContaining(@Param("role") Role role);

    /**
     * Find users by username containing (case-insensitive search)
     */
    List<User> findByUsernameContainingIgnoreCase(String username);

    /**
     * Find user by username and active status
     */
    Optional<User> findByUsernameAndActive(String username, boolean active);
}
