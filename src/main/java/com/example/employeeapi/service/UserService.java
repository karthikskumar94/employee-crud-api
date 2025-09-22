package com.example.employeeapi.service;

import com.example.employeeapi.entity.User;
import com.example.employeeapi.enums.Role;
import com.example.employeeapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new user
     */
    public User createUser(User user) {
        // Check if username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("User with username '" + user.getUsername() + "' already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User with email '" + user.getEmail() + "' already exists");
        }

        logger.info("Creating new user: {}", user.getUsername());
        return userRepository.save(user);
    }

    /**
     * Authenticate user by username and password
     */
    @Transactional(readOnly = true)
    public Optional<User> authenticateUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsernameAndActive(username, true);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // In a real application, you would use password hashing (BCrypt, etc.)
            // For demo purposes, we're using plain text comparison
            if (password.equals(user.getPassword())) {
                logger.info("User authenticated successfully: {}", username);
                return Optional.of(user);
            } else {
                logger.warn("Invalid password for user: {}", username);
            }
        } else {
            logger.warn("User not found or inactive: {}", username);
        }
        
        return Optional.empty();
    }

    /**
     * Get all users
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get all active users
     */
    @Transactional(readOnly = true)
    public List<User> getActiveUsers() {
        return userRepository.findByActiveTrue();
    }

    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Get user by username
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Get user by email
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Get users by role
     */
    @Transactional(readOnly = true)
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRolesContaining(role);
    }

    /**
     * Update user
     */
    public User updateUser(Long id, User userDetails) {
        return userRepository.findById(id)
                .map(user -> {
                    // Check if username is being changed and if new username already exists
                    if (!user.getUsername().equals(userDetails.getUsername()) &&
                        userRepository.existsByUsername(userDetails.getUsername())) {
                        throw new RuntimeException("Username '" + userDetails.getUsername() + "' already exists");
                    }

                    // Check if email is being changed and if new email already exists
                    if (!user.getEmail().equals(userDetails.getEmail()) &&
                        userRepository.existsByEmail(userDetails.getEmail())) {
                        throw new RuntimeException("Email '" + userDetails.getEmail() + "' already exists");
                    }

                    user.setUsername(userDetails.getUsername());
                    user.setEmail(userDetails.getEmail());
                    user.setFullName(userDetails.getFullName());
                    user.setRoles(userDetails.getRoles());
                    user.setActive(userDetails.isActive());

                    logger.info("Updated user: {}", user.getUsername());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    /**
     * Update user roles
     */
    public User updateUserRoles(Long id, Set<Role> roles) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setRoles(roles);
                    logger.info("Updated roles for user {}: {}", user.getUsername(), roles);
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    /**
     * Deactivate user (soft delete)
     */
    public boolean deactivateUser(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setActive(false);
                    userRepository.save(user);
                    logger.info("Deactivated user: {}", user.getUsername());
                    return true;
                })
                .orElse(false);
    }

    /**
     * Activate user
     */
    public boolean activateUser(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setActive(true);
                    userRepository.save(user);
                    logger.info("Activated user: {}", user.getUsername());
                    return true;
                })
                .orElse(false);
    }

    /**
     * Delete user permanently
     */
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            logger.info("Permanently deleted user with id: {}", id);
            return true;
        }
        return false;
    }

    /**
     * Check if user exists
     */
    @Transactional(readOnly = true)
    public boolean userExists(Long id) {
        return userRepository.existsById(id);
    }

    /**
     * Get total count of users
     */
    @Transactional(readOnly = true)
    public long getTotalUserCount() {
        return userRepository.count();
    }

    /**
     * Get count of active users
     */
    @Transactional(readOnly = true)
    public long getActiveUserCount() {
        return userRepository.findByActiveTrue().size();
    }

    /**
     * Search users by username
     */
    @Transactional(readOnly = true)
    public List<User> searchUsersByUsername(String username) {
        return userRepository.findByUsernameContainingIgnoreCase(username);
    }
}
