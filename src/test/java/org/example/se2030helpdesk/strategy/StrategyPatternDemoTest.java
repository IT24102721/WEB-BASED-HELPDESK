package org.example.se2030helpdesk.strategy;

import org.example.se2030helpdesk.model.User;
import org.example.se2030helpdesk.model.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class StrategyPatternDemoTest {

    @Autowired
    private UserManagementContext userManagementContext;

    @Test
    public void demonstrateStrategyPattern() {
        System.out.println("=== STRATEGY PATTERN DEMONSTRATION ===");
        
        // Create test users
        User student = createUser(1L, "student1", UserRole.STUDENT);
        User admin = createUser(2L, "admin1", UserRole.ADMIN);
        User superAdmin = createUser(3L, "superadmin1", UserRole.SUPER_ADMIN);
        
        List<User> allUsers = Arrays.asList(student, admin, superAdmin);
        
        // Demonstrate Student Strategy
        System.out.println("\n--- STUDENT STRATEGY ---");
        List<User> studentManageable = userManagementContext.getManageableUsers(allUsers, student);
        System.out.println("Student can manage: " + studentManageable.size() + " users");
        assertEquals(1, studentManageable.size(), "Student should only see themselves");
        assertTrue(userManagementContext.canViewUser(student, student), "Student can view themselves");
        assertFalse(userManagementContext.canViewUser(student, admin), "Student cannot view admin");
        assertFalse(userManagementContext.canDeleteUser(student, student), "Student cannot delete anyone");
        
        // Demonstrate Admin Strategy
        System.out.println("\n--- ADMIN STRATEGY ---");
        List<User> adminManageable = userManagementContext.getManageableUsers(allUsers, admin);
        System.out.println("Admin can manage: " + adminManageable.size() + " users");
        assertEquals(2, adminManageable.size(), "Admin should see student and admin, but not super admin");
        assertTrue(userManagementContext.canViewUser(admin, student), "Admin can view student");
        assertTrue(userManagementContext.canViewUser(admin, admin), "Admin can view admin");
        assertFalse(userManagementContext.canViewUser(admin, superAdmin), "Admin cannot view super admin");
        assertTrue(userManagementContext.canDeleteUser(admin, student), "Admin can delete student");
        assertFalse(userManagementContext.canDeleteUser(admin, admin), "Admin cannot delete other admin");
        
        // Demonstrate Super Admin Strategy
        System.out.println("\n--- SUPER ADMIN STRATEGY ---");
        List<User> superAdminManageable = userManagementContext.getManageableUsers(allUsers, superAdmin);
        System.out.println("Super Admin can manage: " + superAdminManageable.size() + " users");
        assertEquals(3, superAdminManageable.size(), "Super admin should see all users");
        assertTrue(userManagementContext.canViewUser(superAdmin, student), "Super admin can view student");
        assertTrue(userManagementContext.canViewUser(superAdmin, admin), "Super admin can view admin");
        assertTrue(userManagementContext.canViewUser(superAdmin, superAdmin), "Super admin can view super admin");
        assertTrue(userManagementContext.canDeleteUser(superAdmin, student), "Super admin can delete student");
        assertTrue(userManagementContext.canDeleteUser(superAdmin, admin), "Super admin can delete admin");
        assertFalse(userManagementContext.canDeleteUser(superAdmin, superAdmin), "Super admin cannot delete themselves");
        
        System.out.println("\n✅ Strategy Pattern is working correctly!");
        System.out.println("✅ Different roles get different permissions!");
        System.out.println("✅ Students see only themselves!");
        System.out.println("✅ Admins see students and other admins!");
        System.out.println("✅ Super admins see everyone!");
    }

    private User createUser(Long id, String username, UserRole role) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setRole(role);
        user.setActive(true);
        return user;
    }
}
