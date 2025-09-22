package com.example.employeeapi.repository;

import com.example.employeeapi.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Custom query methods
    Optional<Employee> findByEmail(String email);
    
    List<Employee> findByDepartment(String department);
    
    List<Employee> findByNameContainingIgnoreCase(String name);

    boolean existsByEmail(String email);
}
