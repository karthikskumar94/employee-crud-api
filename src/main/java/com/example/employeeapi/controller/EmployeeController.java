package com.example.employeeapi.controller;

import com.example.employeeapi.entity.Employee;
import com.example.employeeapi.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // Create a new employee
    @PostMapping
    public ResponseEntity<?> createEmployee(@Valid @RequestBody Employee employee) {
        try {
            Employee savedEmployee = employeeService.createEmployee(employee);
            return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.CONFLICT);
        }
    }

    // Get all employees
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    // Get employee by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        if (employee.isPresent()) {
            return new ResponseEntity<>(employee.get(), HttpStatus.OK);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Employee not found with id: " + id);
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

    // Get employee by email
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getEmployeeByEmail(@PathVariable String email) {
        Optional<Employee> employee = employeeService.getEmployeeByEmail(email);
        if (employee.isPresent()) {
            return new ResponseEntity<>(employee.get(), HttpStatus.OK);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Employee not found with email: " + email);
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

    // Get employees by department
    @GetMapping("/department/{department}")
    public ResponseEntity<List<Employee>> getEmployeesByDepartment(@PathVariable String department) {
        List<Employee> employees = employeeService.getEmployeesByDepartment(department);
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    // Search employees by name
    @GetMapping("/search")
    public ResponseEntity<List<Employee>> searchEmployeesByName(@RequestParam String name) {
        List<Employee> employees = employeeService.searchEmployeesByName(name);
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    // Update employee
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @Valid @RequestBody Employee employeeDetails) {
        try {
            Employee updatedEmployee = employeeService.updateEmployee(id, employeeDetails);
            return new ResponseEntity<>(updatedEmployee, HttpStatus.OK);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(error, HttpStatus.CONFLICT);
            }
        }
    }

    // Delete employee
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        boolean deleted = employeeService.deleteEmployee(id);
        if (deleted) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Employee deleted successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Employee not found with id: " + id);
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

    // Get employee count
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getEmployeeCount() {
        long count = employeeService.getTotalEmployeeCount();
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Employee API");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
