package com.example.employeeapi;

import com.example.employeeapi.entity.Employee;
import com.example.employeeapi.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (employeeRepository.count() == 0) {
            // Create sample employees
            Employee emp1 = new Employee("John Doe", "john.doe@example.com", "Engineering", new BigDecimal("75000.00"));
            Employee emp2 = new Employee("Jane Smith", "jane.smith@example.com", "Marketing", new BigDecimal("65000.00"));
            Employee emp3 = new Employee("Mike Johnson", "mike.johnson@example.com", "Engineering", new BigDecimal("80000.00"));
            Employee emp4 = new Employee("Sarah Wilson", "sarah.wilson@example.com", "HR", new BigDecimal("60000.00"));
            Employee emp5 = new Employee("David Brown", "david.brown@example.com", "Finance", new BigDecimal("70000.00"));

            // Save sample employees
            employeeRepository.save(emp1);
            employeeRepository.save(emp2);
            employeeRepository.save(emp3);
            employeeRepository.save(emp4);
            employeeRepository.save(emp5);

            System.out.println("Sample employee data loaded successfully!");
        }
    }
}
