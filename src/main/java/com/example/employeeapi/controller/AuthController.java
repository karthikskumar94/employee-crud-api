package com.example.employeeapi.controller;

import com.example.employeeapi.entity.User;
import com.example.employeeapi.security.JwtUtil;
import com.example.employeeapi.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthController(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");

            if (username == null || password == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Username and password are required");
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            }

            // Authenticate user using UserService
            Optional<User> userOpt = userService.authenticateUser(username, password);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                // Generate JWT token with username and roles
                String token = jwtUtil.generateToken(user.getUsername(), user.getRoles());
                
                // Prepare response with user info and token
                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("username", user.getUsername());
                response.put("fullName", user.getFullName());
                response.put("roles", user.getRoles());
                response.put("message", "Authentication successful");
                
                logger.info("User {} authenticated successfully with roles: {}", username, user.getRoles());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid username or password");
                logger.warn("Authentication failed for username: {}", username);
                return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            logger.error("Error during authentication", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Authentication failed");
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
