package com.example.employeeapi.enums;

/**
 * Enum representing different user roles in the system
 */
public enum Role {
    ADMIN("ADMIN"),
    HR("HR"), 
    MANAGER("MANAGER"),
    EMPLOYEE("EMPLOYEE");

    private final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    @Override
    public String toString() {
        return roleName;
    }
}
