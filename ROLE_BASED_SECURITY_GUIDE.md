# Role-Based Security with Spring AOP - Complete Guide

## Overview

This implementation provides **role-based authentication and authorization** using **Spring AOP (Aspect-Oriented Programming)** with JWT tokens. The system supports fine-grained access control through custom annotations.

## Architecture Components

### 1. **Role Enum** (`Role.java`)
```java
public enum Role {
    ADMIN("ADMIN"),      // Full system access
    HR("HR"),            // HR operations
    MANAGER("MANAGER"),  // Management operations  
    EMPLOYEE("EMPLOYEE") // Basic employee access
}
```

### 2. **User Entity** (`User.java`)
- Stores user credentials and role assignments
- Supports multiple roles per user
- Includes audit fields (created_at, updated_at)

### 3. **Custom Annotation** (`@RequireRole`)
```java
@RequireRole({Role.ADMIN, Role.HR})          // Requires ADMIN OR HR
@RequireRole(value = {Role.ADMIN}, requireAll = true) // Requires ALL roles
```

### 4. **AOP Security Aspect** (`SecurityAspect.java`)
- Intercepts method calls with `@RequireRole`
- Validates JWT tokens and user roles
- Provides detailed logging and error handling

### 5. **Enhanced JWT Utility** (`JwtUtil.java`)
- Generates tokens with embedded roles
- Extracts roles from tokens for validation
- Supports role-based token operations

## 🔐 User Roles & Permissions

### **ADMIN** - System Administrator
- ✅ Create/Read/Update/Delete employees
- ✅ Access all endpoints
- ✅ View employee counts and statistics

### **HR** - Human Resources
- ✅ Create/Read/Update employees  
- ✅ View all employee data
- ✅ Access employee counts
- ❌ Cannot delete employees

### **MANAGER** - Department Manager
- ✅ Read employee data (all employees)
- ✅ Search employees by name/department
- ❌ Cannot create/update/delete employees

### **EMPLOYEE** - Regular Employee
- ✅ View individual employee details
- ✅ Access health check
- ❌ Limited access to bulk operations

## 📊 Endpoint Access Matrix

| Endpoint | ADMIN | HR | MANAGER | EMPLOYEE |
|----------|-------|----|---------|---------| 
| `POST /api/employees` (Create) | ✅ | ✅ | ❌ | ❌ |
| `GET /api/employees` (List All) | ✅ | ✅ | ✅ | ❌ |
| `GET /api/employees/{id}` | ✅ | ✅ | ✅ | ✅ |
| `GET /api/employees/email/{email}` | ✅ | ✅ | ✅ | ❌ |
| `PUT /api/employees/{id}` (Update) | ✅ | ✅ | ❌ | ❌ |
| `DELETE /api/employees/{id}` | ✅ | ❌ | ❌ | ❌ |
| `GET /api/employees/department/{dept}` | ✅ | ✅ | ✅ | ❌ |
| `GET /api/employees/search` | ✅ | ✅ | ✅ | ❌ |
| `GET /api/employees/count` | ✅ | ✅ | ❌ | ❌ |
| `GET /api/employees/health` | ✅ | ✅ | ✅ | ✅ |

## 👥 Sample User Accounts

The system creates these test users automatically:

| Username | Password | Roles | Use Case |
|----------|----------|-------|----------|
| `admin` | `password` | ADMIN | Full system access |
| `hr_manager` | `password` | HR | HR operations |
| `manager` | `password` | MANAGER | Management tasks |
| `employee` | `password` | EMPLOYEE | Basic access |
| `hr_admin` | `password` | HR, ADMIN | Combined privileges |
| `team_lead` | `password` | MANAGER, EMPLOYEE | Team leadership |

## 🚀 How to Use

### **Step 1: Start the Application**
```bash
./gradlew bootRun
```

### **Step 2: Login to Get JWT Token**
```bash
POST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin",
  "fullName": "System Administrator",
  "roles": ["ADMIN"],
  "message": "Authentication successful"
}
```

### **Step 3: Use Token for Protected Endpoints**
```bash
GET /api/employees
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

## 🔧 AOP Implementation Details

### **@RequireRole Annotation Usage**

#### **Single Role:**
```java
@RequireRole(Role.ADMIN)
public void adminOnlyMethod() { }
```

#### **Multiple Roles (OR logic):**
```java
@RequireRole({Role.ADMIN, Role.HR}) // User needs ADMIN OR HR
public void hrOrAdminMethod() { }
```

#### **Multiple Roles (AND logic):**
```java
@RequireRole(value = {Role.HR, Role.MANAGER}, requireAll = true)
public void hrAndManagerMethod() { } // User needs BOTH roles
```

#### **Custom Error Message:**
```java
@RequireRole(value = Role.ADMIN, message = "Only administrators can access this resource")
public void customErrorMethod() { }
```

### **AOP Aspect Processing Flow**

1. **Method Interception**: AOP intercepts calls to `@RequireRole` methods
2. **Token Extraction**: Extracts JWT token from `Authorization` header
3. **Token Validation**: Validates token signature and expiration
4. **Role Extraction**: Extracts user roles from JWT claims
5. **Permission Check**: Verifies user has required roles
6. **Access Decision**: Allows or denies method execution

## 🧪 Testing Different Scenarios

### **Test 1: Admin Access (Full Permissions)**
```bash
# Login as admin
POST /auth/login
{"username": "admin", "password": "password"}

# Try any endpoint - should work
GET /api/employees
DELETE /api/employees/1
```

### **Test 2: HR Access (Create/Read/Update)**
```bash
# Login as HR
POST /auth/login
{"username": "hr_manager", "password": "password"}

# Should work
POST /api/employees
GET /api/employees

# Should fail (403 Forbidden)
DELETE /api/employees/1
```

### **Test 3: Employee Access (Limited)**
```bash
# Login as employee
POST /auth/login
{"username": "employee", "password": "password"}

# Should work
GET /api/employees/1
GET /api/employees/health

# Should fail (403 Forbidden)
GET /api/employees
POST /api/employees
```

### **Test 4: No Token (Unauthorized)**
```bash
# Try without Authorization header
GET /api/employees
# Response: 401 Unauthorized
```

### **Test 5: Invalid Token**
```bash
# Use expired/invalid token
GET /api/employees
Authorization: Bearer invalid_token_here
# Response: 401 Unauthorized
```

## 🛠️ Configuration

### **Enable AOP in Security Config**
```java
@Configuration
@EnableWebSecurity
@EnableAspectJAutoProxy  // ← Enables AOP
public class SecurityConfig { }
```

### **JWT Token Structure**
```json
{
  "sub": "admin",           // Username
  "roles": ["ADMIN", "HR"], // User roles
  "iat": 1634567890,        // Issued at
  "exp": 1634571490         // Expires at
}
```

## 🚨 Error Responses

### **403 Forbidden (Insufficient Privileges)**
```json
{
  "timestamp": "2023-10-18T10:30:00Z",
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied: insufficient privileges"
}
```

### **401 Unauthorized (No/Invalid Token)**
```json
{
  "timestamp": "2023-10-18T10:30:00Z", 
  "status": 401,
  "error": "Unauthorized",
  "message": "Authentication required"
}
```

## 🎯 Best Practices Implemented

1. **Separation of Concerns**: Authentication vs Authorization
2. **Clean Code**: Annotation-based security
3. **Flexible Roles**: Support for multiple roles per user
4. **Detailed Logging**: Comprehensive audit trail
5. **Error Handling**: Proper HTTP status codes
6. **Token Security**: JWT with role claims
7. **Database Design**: Normalized role storage

## 🔍 Debugging & Troubleshooting

### **Enable Debug Logging**
```yaml
logging:
  level:
    com.example.employeeapi.aspect.SecurityAspect: DEBUG
    com.example.employeeapi.security.JwtUtil: DEBUG
```

### **Common Issues**

**Issue**: 403 Forbidden despite valid token
- **Check**: User has required role in database
- **Verify**: Token contains correct role claims

**Issue**: 401 Unauthorized with token
- **Check**: Token format (starts with "Bearer ")
- **Verify**: Token not expired
- **Validate**: JWT secret matches

**Issue**: AOP not intercepting methods
- **Check**: `@EnableAspectJAutoProxy` annotation
- **Verify**: Method is public and in Spring-managed bean
- **Confirm**: `@RequireRole` annotation present

## 📈 Performance Considerations

- **Token Caching**: Consider caching valid tokens
- **Role Caching**: Cache user roles to reduce DB queries
- **AOP Overhead**: Minimal performance impact
- **Database Indexing**: Index on username and roles columns

---

## 🎉 Implementation Complete!

Your Employee CRUD API now has **enterprise-grade role-based security** with:

✅ **JWT Authentication** with embedded roles  
✅ **AOP-based Authorization** using custom annotations  
✅ **Flexible Role System** supporting multiple roles  
✅ **Comprehensive Logging** and error handling  
✅ **Sample Users** for immediate testing  
✅ **Fine-grained Permissions** per endpoint  

**Start the application and test with different user roles!** 🚀

