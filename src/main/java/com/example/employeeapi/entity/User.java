package com.example.employeeapi.entity;

import com.example.employeeapi.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Password is required")
    @Column(name = "password", nullable = false)
    private String password;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Full name is required")
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    @NotNull(message = "At least one role is required")
    private Set<Role> roles;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Default constructor
    public User() {
        this.createdAt = LocalDateTime.now();
    }

    // Constructor with parameters
    public User(String username, String password, String email, String fullName, Set<Role> roles) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.roles = roles;
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Utility methods
    public boolean hasRole(Role role) {
        return this.roles != null && this.roles.contains(role);
    }

    public boolean hasAnyRole(Role... roles) {
        if (this.roles == null) return false;
        for (Role role : roles) {
            if (this.roles.contains(role)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", roles=" + roles +
                ", active=" + active +
                ", createdAt=" + createdAt +
                '}';
    }
}
