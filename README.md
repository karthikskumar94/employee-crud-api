# Employee CRUD API

A Spring Boot REST API that performs CRUD (Create, Read, Update, Delete) operations on an Employee entity using Spring Data JPA and H2 Database.

## Features

- **Spring Boot 3.3.5** with Java 21+ compatibility
- **H2 In-Memory Database** for easy testing
- **Spring Data JPA** for database operations
- **Bean Validation** for input validation
- **RESTful API Design** with proper HTTP status codes
- **Sample data preloading** for immediate testing
- **H2 Console** for database inspection

## Technologies Used

- Spring Boot 3.3.5
- Spring Data JPA
- Spring Boot Starter Web
- Spring Boot Starter Validation
- H2 Database
- Gradle Build Tool
- Java 21+

## Employee Entity

The Employee entity contains the following fields:
- `id` (Long) - Auto-generated primary key
- `name` (String) - Employee name (required)
- `email` (String) - Employee email (required, unique, validated)
- `department` (String) - Department name (required)
- `salary` (BigDecimal) - Employee salary (required, must be positive)

## Getting Started

### Prerequisites

- Java 21 or higher
- Gradle (or use the included Gradle wrapper)

### Running the Application

1. Clone or navigate to the project directory
2. Build the application:
   ```bash
   .\gradlew.bat build
   ```
3. Run the application:
   ```bash
   .\gradlew.bat bootRun
   ```
4. The application will start on port 8080

### Database Access

- **H2 Console**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: `password`

## API Endpoints

### Base URL: `http://localhost:8080/api/employees`

### 1. Create Employee
- **POST** `/api/employees`
- **Body**: JSON Employee object
- **Example**:
  ```json
  {
    "name": "John Doe",
    "email": "john.doe@example.com",
    "department": "Engineering",
    "salary": 75000.00
  }
  ```

### 2. Get All Employees
- **GET** `/api/employees`
- **Response**: Array of Employee objects

### 3. Get Employee by ID
- **GET** `/api/employees/{id}`
- **Response**: Employee object or 404 if not found

### 4. Get Employee by Email
- **GET** `/api/employees/email/{email}`
- **Response**: Employee object or 404 if not found

### 5. Get Employees by Department
- **GET** `/api/employees/department/{department}`
- **Response**: Array of Employee objects

### 6. Search Employees by Name
- **GET** `/api/employees/search?name={searchTerm}`
- **Response**: Array of Employee objects

### 7. Update Employee
- **PUT** `/api/employees/{id}`
- **Body**: JSON Employee object with updated data

### 8. Delete Employee
- **DELETE** `/api/employees/{id}`
- **Response**: Success message or 404 if not found

### 9. Get Employee Count
- **GET** `/api/employees/count`
- **Response**: `{"count": number}`

### 10. Health Check
- **GET** `/api/employees/health`
- **Response**: `{"status": "UP", "service": "Employee API"}`

## Sample Data

The application comes preloaded with 5 sample employees:
- John Doe (Engineering)
- Jane Smith (Marketing)
- Mike Johnson (Engineering)
- Sarah Wilson (HR)
- David Brown (Finance)

## Testing with cURL

### Create a new employee:
```bash
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Alice Johnson",
    "email": "alice.johnson@example.com",
    "department": "Sales",
    "salary": 68000.00
  }'
```

### Get all employees:
```bash
curl http://localhost:8080/api/employees
```

### Get employee by ID:
```bash
curl http://localhost:8080/api/employees/1
```

### Update employee:
```bash
curl -X PUT http://localhost:8080/api/employees/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Smith",
    "email": "john.smith@example.com",
    "department": "Engineering",
    "salary": 80000.00
  }'
```

### Delete employee:
```bash
curl -X DELETE http://localhost:8080/api/employees/1
```

## Error Handling

The API returns appropriate HTTP status codes:
- **200 OK** - Successful GET, PUT, DELETE operations
- **201 CREATED** - Successful POST operations
- **404 NOT FOUND** - Resource not found
- **409 CONFLICT** - Email already exists
- **400 BAD REQUEST** - Validation errors

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/example/employeeapi/
│   │       ├── EmployeeApiApplication.java
│   │       ├── DataLoader.java
│   │       ├── controller/
│   │       │   └── EmployeeController.java
│   │       ├── entity/
│   │       │   └── Employee.java
│   │       ├── repository/
│   │       │   └── EmployeeRepository.java
│   │       └── service/
│   │           └── EmployeeService.java
│   └── resources/
│       └── application.yml
└── build.gradle
```

## Configuration

The application is configured in `application.yml`:
- H2 database settings
- JPA/Hibernate configuration
- Server port (8080)
- Logging levels

Spring Security Integration
✅ Overview

This project integrates Spring Security into a Spring Boot application to secure the API endpoints. The configuration uses in-memory authentication with predefined users and roles.

✅ Dependencies

The following dependency is added to build.gradle:

dependencies {
implementation 'org.springframework.boot:spring-boot-starter-security'
}


This ensures Spring Security is enabled for authentication and authorization.

## Security Configuration

A SecurityConfig class is created to define the security rules:

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/employees/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(Customizer.withDefaults())
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


A UserDetailsService bean is configured in UserConfig.java:

@Configuration
public class UserConfig {

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("admin")
                .password(passwordEncoder.encode("adminpass"))
                .roles("ADMIN")
                .build());
        manager.createUser(User.withUsername("user")
                .password(passwordEncoder.encode("userpass"))
                .roles("USER")
                .build());
        return manager;
    }
}

✅ Users and Roles
Username	Password	Roles
admin	adminpass	ADMIN
user	userpass	USER

Only users with ADMIN role can access /api/employees/**.

Other requests require authentication but are accessible depending on role permissions.

✅ Running the Application

Build the project:

./gradlew clean build


Run the application:

./gradlew bootRun


The application will start on http://localhost:8080.

✅ Testing the Endpoints
Using a Browser:

Visit http://localhost:8080/api/employees.

Enter the username and password when prompted.

Use admin / adminpass to access employee data.

Using user / userpass will result in a "403 Forbidden" error.

Using Postman or curl:

Request URL: http://localhost:8080/api/employees

Method: GET

Authentication: Basic Auth

Example curl command:

curl -u admin:adminpass http://localhost:8080/api/employees


You should see the employee data if the credentials are correct.

✅ Notes

CSRF protection is disabled for development simplicity.

Default login and HTTP Basic authentication are used.

This setup uses in-memory authentication and is intended for development and demonstration purposes.

For production, you should implement database-backed authentication and stronger security controls.
