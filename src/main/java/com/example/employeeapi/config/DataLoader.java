package com.example.employeeapi.config;

import com.example.employeeapi.entity.Employee;
import com.example.employeeapi.entity.User;
import com.example.employeeapi.enums.Role;
import com.example.employeeapi.repository.EmployeeRepository;
import com.example.employeeapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        loadSampleUsers();
        loadSampleEmployees();
    }

    private void loadSampleUsers() {
        // Check if users already exist
        if (userRepository.count() > 0) {
            logger.info("Users already exist, skipping sample data creation");
            return;
        }

        logger.info("Creating sample users with different roles...");

        // Create Admin user
        User admin = new User(
                "admin",
                "password", // In production, this should be hashed
                "admin@company.com",
                "System Administrator",
                Set.of(Role.ADMIN)
        );

        // Create HR user
        User hr = new User(
                "hr_manager",
                "password",
                "hr@company.com",
                "HR Manager",
                Set.of(Role.HR)
        );

        // Create Manager user
        User manager = new User(
                "manager",
                "password",
                "manager@company.com",
                "Department Manager",
                Set.of(Role.MANAGER)
        );

        // Create Employee user
        User employee = new User(
                "employee",
                "password",
                "employee@company.com",
                "Regular Employee",
                Set.of(Role.EMPLOYEE)
        );

        // Create HR Admin user (multiple roles)
        User hrAdmin = new User(
                "hr_admin",
                "password",
                "hr.admin@company.com",
                "HR Administrator",
                Set.of(Role.HR, Role.ADMIN)
        );

        // Create Manager with Employee role
        User managerEmployee = new User(
                "team_lead",
                "password",
                "team.lead@company.com",
                "Team Leader",
                Set.of(Role.MANAGER, Role.EMPLOYEE)
        );

        // Save all users
        userRepository.save(admin);
        userRepository.save(hr);
        userRepository.save(manager);
        userRepository.save(employee);
        userRepository.save(hrAdmin);
        userRepository.save(managerEmployee);

        logger.info("Created {} sample users with different roles", userRepository.count());
        logger.info("Sample user credentials:");
        logger.info("  Admin: username=admin, password=password, roles=[ADMIN]");
        logger.info("  HR Manager: username=hr_manager, password=password, roles=[HR]");
        logger.info("  Manager: username=manager, password=password, roles=[MANAGER]");
        logger.info("  Employee: username=employee, password=password, roles=[EMPLOYEE]");
        logger.info("  HR Admin: username=hr_admin, password=password, roles=[HR, ADMIN]");
        logger.info("  Team Lead: username=team_lead, password=password, roles=[MANAGER, EMPLOYEE]");
    }

    private void loadSampleEmployees() {
        // Check if employees already exist
        if (employeeRepository.count() > 0) {
            logger.info("Employees already exist, skipping sample data creation");
            return;
        }

        logger.info("Creating sample employee data...");

        // Create sample employees
        Employee emp1 = new Employee("John Doe", "john.doe@example.com", "Engineering", new BigDecimal("75000.00"));
        Employee emp2 = new Employee("Jane Smith", "jane.smith@example.com", "Marketing", new BigDecimal("65000.00"));
        Employee emp3 = new Employee("Bob Johnson", "bob.johnson@example.com", "Engineering", new BigDecimal("80000.00"));
        Employee emp4 = new Employee("Alice Brown", "alice.brown@example.com", "HR", new BigDecimal("60000.00"));
        Employee emp5 = new Employee("Charlie Wilson", "charlie.wilson@example.com", "Finance", new BigDecimal("70000.00"));

        // Save all employees
        employeeRepository.save(emp1);
        employeeRepository.save(emp2);
        employeeRepository.save(emp3);
        employeeRepository.save(emp4);
        employeeRepository.save(emp5);

        logger.info("Sample employee data loaded successfully!");
    }
}
