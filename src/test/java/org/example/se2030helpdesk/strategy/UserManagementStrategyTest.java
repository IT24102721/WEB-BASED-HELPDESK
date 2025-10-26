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
public class UserManagementStrategyTest {

    @Autowired
    private UserManagementContext userManagementContext;

    @Test
    public void testStudentStrategy() {
        // Create test users
        User student = createUser(1L, "student1", UserRole.STUDENT);
        User anotherStudent = createUser(2L, "student2", UserRole.STUDENT);
        User admin = createUser(3L, "admin1", UserRole.ADMIN);
        
        List<User> allUsers = Arrays.asList(student, anotherStudent, admin);
        
        // Test student can only see themselves
        List<User> manageableUsers = userManagementContext.getManageableUsers(allUsers, student);
        assertEquals(1, manageableUsers.size());
        assertEquals(student.getId(), manageableUsers.get(0).getId());
        
        // Test student can view themselves
        assertTrue(userManagementContext.canViewUser(student, student));
        
        // Test student cannot view other users
        assertFalse(userManagementContext.canViewUser(student, anotherStudent));
        assertFalse(userManagementContext.canViewUser(student, admin));
        
        // Test student can update themselves
        assertTrue(userManagementContext.canUpdateUser(student, student));
        
        // Test student cannot update others
        assertFalse(userManagementContext.canUpdateUser(student, anotherStudent));
        assertFalse(userManagementContext.canUpdateUser(student, admin));
        
        // Test student cannot delete anyone
        assertFalse(userManagementContext.canDeleteUser(student, student));
        assertFalse(userManagementContext.canDeleteUser(student, anotherStudent));
    }

    @Test
    public void testAdminStrategy() {
        // Create test users
        User admin = createUser(1L, "admin1", UserRole.ADMIN);
        User student = createUser(2L, "student1", UserRole.STUDENT);
        User superAdmin = createUser(3L, "superadmin1", UserRole.SUPER_ADMIN);
        
        List<User> allUsers = Arrays.asList(admin, student, superAdmin);
        
        // Test admin can manage students and other admins, but not super admins
        List<User> manageableUsers = userManagementContext.getManageableUsers(allUsers, admin);
        assertEquals(2, manageableUsers.size()); // admin and student, but not super admin
        
        // Test admin can view students
        assertTrue(userManagementContext.canViewUser(admin, student));
        
        // Test admin cannot view super admin
        assertFalse(userManagementContext.canViewUser(admin, superAdmin));
        
        // Test admin can update students
        assertTrue(userManagementContext.canUpdateUser(admin, student));
        
        // Test admin cannot update super admin
        assertFalse(userManagementContext.canUpdateUser(admin, superAdmin));
        
        // Test admin can delete students
        assertTrue(userManagementContext.canDeleteUser(admin, student));
        
        // Test admin cannot delete super admin
        assertFalse(userManagementContext.canDeleteUser(admin, superAdmin));
    }

    @Test
    public void testSuperAdminStrategy() {
        // Create test users
        User superAdmin = createUser(1L, "superadmin1", UserRole.SUPER_ADMIN);
        User admin = createUser(2L, "admin1", UserRole.ADMIN);
        User student = createUser(3L, "student1", UserRole.STUDENT);
        
        List<User> allUsers = Arrays.asList(superAdmin, admin, student);
        
        // Test super admin can manage all users
        List<User> manageableUsers = userManagementContext.getManageableUsers(allUsers, superAdmin);
        assertEquals(3, manageableUsers.size());
        
        // Test super admin can view all users
        assertTrue(userManagementContext.canViewUser(superAdmin, admin));
        assertTrue(userManagementContext.canViewUser(superAdmin, student));
        
        // Test super admin can update all users
        assertTrue(userManagementContext.canUpdateUser(superAdmin, admin));
        assertTrue(userManagementContext.canUpdateUser(superAdmin, student));
        
        // Test super admin can delete all users except themselves
        assertTrue(userManagementContext.canDeleteUser(superAdmin, admin));
        assertTrue(userManagementContext.canDeleteUser(superAdmin, student));
        assertFalse(userManagementContext.canDeleteUser(superAdmin, superAdmin));
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
