package com.example.employeeapi.security;

import com.example.employeeapi.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private final SecretKey key;

    // secret loaded from application.yml or properties
    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String username) {
        return generateToken(username, Collections.emptySet());
    }

    public String generateToken(String username, Set<Role> roles) {
        Map<String, Object> claims = new HashMap<>();
        if (roles != null && !roles.isEmpty()) {
            List<String> roleNames = roles.stream()
                    .map(Role::getRoleName)
                    .collect(Collectors.toList());
            claims.put("roles", roleNames);
        }
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hr
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (username.equals(extractedUsername) && !isTokenExpired(token));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Set<Role> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        @SuppressWarnings("unchecked")
        List<String> roleNames = (List<String>) claims.get("roles");
        
        if (roleNames == null || roleNames.isEmpty()) {
            return Collections.emptySet();
        }
        
        return roleNames.stream()
                .map(roleName -> {
                    try {
                        return Role.valueOf(roleName);
                    } catch (IllegalArgumentException e) {
                        return null; // Skip invalid role names
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public boolean hasRole(String token, Role role) {
        Set<Role> userRoles = extractRoles(token);
        return userRoles.contains(role);
    }

    public boolean hasAnyRole(String token, Role... roles) {
        Set<Role> userRoles = extractRoles(token);
        return Arrays.stream(roles).anyMatch(userRoles::contains);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
