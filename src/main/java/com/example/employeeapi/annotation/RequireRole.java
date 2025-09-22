package com.example.employeeapi.annotation;

import com.example.employeeapi.enums.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify required roles for accessing a method or class
 * Uses Spring AOP for role-based authorization
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    
    /**
     * Required roles to access the annotated method/class
     * User must have at least one of the specified roles
     */
    Role[] value();
    
    /**
     * Whether user must have ALL specified roles (true) or just ONE (false)
     * Default is false (requires only one role)
     */
    boolean requireAll() default false;
    
    /**
     * Custom error message when access is denied
     */
    String message() default "Access denied: insufficient privileges";
}
