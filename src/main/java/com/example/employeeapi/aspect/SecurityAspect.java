package com.example.employeeapi.aspect;

import com.example.employeeapi.annotation.RequireRole;
import com.example.employeeapi.entity.AuditLog;
import com.example.employeeapi.entity.User;
import com.example.employeeapi.enums.Role;
import com.example.employeeapi.repository.AuditLogRepository;
import com.example.employeeapi.repository.UserRepository;
import com.example.employeeapi.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;


/**
 * Spring AOP Aspect for role-based security authorization
 * Intercepts methods annotated with @RequireRole and validates user permissions
 */
@Aspect
@Component
public class SecurityAspect {

    private static final Logger logger = LoggerFactory.getLogger(SecurityAspect.class);

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    // Pointcut for any save/update in service layer
    @Pointcut("execution(* com.example.employeeapi.controller.*.createEmployee*(..)) || execution(* com.example.employeeapi.service.*.updateEmployee*(..)) || execution(* com.example.employeeapi.service.*.deleteEmployee*(..))")
    public void transactionMethods() {}

    /**
     * Before advice that runs before any method annotated with @RequireRole
     * Validates if the current user has required roles to access the method
     */
    @Before("@annotation(requireRole)")
    public void checkRoleAccess(JoinPoint joinPoint, RequireRole requireRole) {
        logger.debug("Checking role access for method: {}", joinPoint.getSignature().getName());
        
        try {
            // Get current HTTP request
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                logger.error("No HTTP request context found");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
            }
            
            HttpServletRequest request = attributes.getRequest();
            
            // Extract JWT token from Authorization header
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("No valid Authorization header found");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
            }
            
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            
            // Extract username and roles from JWT token
            String username = jwtUtil.extractUsername(token);
            Set<Role> userRoles = jwtUtil.extractRoles(token);
            
            logger.debug("User: {} has roles: {}", username, userRoles);
            
            // Validate token
            if (!jwtUtil.validateToken(token, username)) {
                logger.warn("Invalid JWT token for user: {}", username);
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
            }
            
            // Check if user has required roles
            Role[] requiredRoles = requireRole.value();
            boolean hasAccess = checkUserRoles(userRoles, requiredRoles, requireRole.requireAll());
            
            if (!hasAccess) {
                logger.warn("Access denied for user: {} to method: {}. Required roles: {}, User roles: {}", 
                    username, joinPoint.getSignature().getName(), Arrays.toString(requiredRoles), userRoles);
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, requireRole.message());
            }
            
            logger.debug("Access granted for user: {} to method: {}", username, joinPoint.getSignature().getName());
            
        } catch (ResponseStatusException e) {
            throw e; // Re-throw HTTP status exceptions
        } catch (Exception e) {
            logger.error("Error during role validation", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Authorization check failed");
        }
    }

    /**
     * Before advice for class-level @RequireRole annotations
     */
    @Before("@within(requireRole) && !@annotation(com.example.employeeapi.annotation.RequireRole)")
    public void checkClassRoleAccess(JoinPoint joinPoint, RequireRole requireRole) {
        // Same logic as method-level, but for class-level annotations
        checkRoleAccess(joinPoint, requireRole);
    }

    /**
     * Check if user roles satisfy the required roles
     */
    private boolean checkUserRoles(Set<Role> userRoles, Role[] requiredRoles, boolean requireAll) {
        if (userRoles == null || userRoles.isEmpty()) {
            return false;
        }
        
        if (requireAll) {
            // User must have ALL required roles
            return userRoles.containsAll(Arrays.asList(requiredRoles));
        } else {
            // User must have at least ONE required role
            return Arrays.stream(requiredRoles)
                    .anyMatch(userRoles::contains);
        }
    }

    /**
     * Get the @RequireRole annotation from method or class
     */
    private RequireRole getRequireRoleAnnotation(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        // First check method-level annotation
        RequireRole annotation = method.getAnnotation(RequireRole.class);
        if (annotation != null) {
            return annotation;
        }
        
        // If not found on method, check class-level annotation
        Class<?> targetClass = joinPoint.getTarget().getClass();
        return targetClass.getAnnotation(RequireRole.class);
    }

    @AfterReturning(value = "transactionMethods()", returning = "result")
    public void logTransaction(JoinPoint joinPoint, Object result) {
        try {
            String methodName = joinPoint.getSignature().getName();
            //String action = methodName.startsWith("createEmployee") ? "CREATE" : "UPDATE";
            String action;
            if (methodName.startsWith("deleteEmployee")) {
                action = "DELETE";
            } else if (methodName.startsWith("createEmployee")) {
                action = "CREATE";
            } else {
                action = "UPDATE";
            }

            Object entity = joinPoint.getArgs()[0]; // assuming first arg is entity
            Long entityId = null;

            // if entity has getId() method
            try {
                entityId = (Long) entity.getClass().getMethod("getId").invoke(entity);
            } catch (Exception ignored) {}


            String username = "system";
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                username = auth.getName();
            }

            // 🔹 Use UserRepository to verify or enrich user info
            Optional<User> userOpt = userRepository.findByUsername(username);
            String finalUsername = userOpt.map(User::getUsername).orElse("unknown");

            AuditLog log = new AuditLog(
                    action,
                    entity.getClass().getSimpleName(),
                    entityId,
                    finalUsername
            );

            auditLogRepository.save(log);

            logger.info("Audit log created for {} on {} by user: {}", action, entity.getClass().getSimpleName(), finalUsername);

        } catch (Exception e) {
            logger.error("Failed to log transaction", e);
        }
    }


}
