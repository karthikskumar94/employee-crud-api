package com.example.employeeapi.service;

import com.example.employeeapi.entity.Employee;
import com.example.employeeapi.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    // Create a new employee
    public Employee createEmployee(Employee employee) {
        // Check if email already exists
        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new RuntimeException("Employee with email " + employee.getEmail() + " already exists");
        }
        return employeeRepository.save(employee);
    }

    // Get all employees
    @Transactional(readOnly = true)
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // Get employee by ID
    @Transactional(readOnly = true)
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    // Get employee by email
    @Transactional(readOnly = true)
    public Optional<Employee> getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

    // Get employees by department
    @Transactional(readOnly = true)
    public List<Employee> getEmployeesByDepartment(String department) {
        return employeeRepository.findByDepartment(department);
    }

    // Search employees by name
    @Transactional(readOnly = true)
    public List<Employee> searchEmployeesByName(String name) {
        return employeeRepository.findByNameContainingIgnoreCase(name);
    }

    // Update employee
    public Employee updateEmployee(Long id, Employee employeeDetails) {
        return employeeRepository.findById(id)
                .map(employee -> {
                    // Check if email is being changed and if new email already exists
                    if (!employee.getEmail().equals(employeeDetails.getEmail()) &&
                        employeeRepository.existsByEmail(employeeDetails.getEmail())) {
                        throw new RuntimeException("Employee with email " + employeeDetails.getEmail() + " already exists");
                    }
                    
                    employee.setName(employeeDetails.getName());
                    employee.setEmail(employeeDetails.getEmail());
                    employee.setDepartment(employeeDetails.getDepartment());
                    employee.setSalary(employeeDetails.getSalary());
                    return employeeRepository.save(employee);
                })
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }

    // Delete employee by ID
    public boolean deleteEmployee(Long id) {
        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Check if employee exists
    @Transactional(readOnly = true)
    public boolean employeeExists(Long id) {
        return employeeRepository.existsById(id);
    }

    // Get total count of employees
    @Transactional(readOnly = true)
    public long getTotalEmployeeCount() {
        return employeeRepository.count();
    }
}
