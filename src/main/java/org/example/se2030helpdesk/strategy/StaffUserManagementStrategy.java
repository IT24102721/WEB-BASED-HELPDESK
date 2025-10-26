package org.example.se2030helpdesk.strategy;

import org.example.se2030helpdesk.model.User;
import org.example.se2030helpdesk.model.UserRole;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Strategy for staff user management.
 * Staff can manage students and some other staff members.
 */
@Component
public class StaffUserManagementStrategy implements UserManagementStrategy {
    
    @Override
    public boolean canHandle(UserRole role) {
        return role == UserRole.STAFF || role == UserRole.FACULTY || 
               role == UserRole.IT_SUPPORT || role == UserRole.COMM_SERVICE;
    }
    
    @Override
    public List<User> getManageableUsers(List<User> allUsers, User currentUser) {
        // Staff can manage students and some staff members, but not admins or super admins
        return allUsers.stream()
                .filter(user -> user.getRole() == UserRole.STUDENT || 
                               user.getRole() == UserRole.STAFF ||
                               user.getRole() == UserRole.FACULTY ||
                               user.getRole() == UserRole.IT_SUPPORT ||
                               user.getRole() == UserRole.COMM_SERVICE)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean canViewUser(User currentUser, User targetUser) {
        // Staff can view students and other staff members
        return targetUser.getRole() == UserRole.STUDENT || 
               targetUser.getRole() == UserRole.STAFF ||
               targetUser.getRole() == UserRole.FACULTY ||
               targetUser.getRole() == UserRole.IT_SUPPORT ||
               targetUser.getRole() == UserRole.COMM_SERVICE;
    }
    
    @Override
    public boolean canUpdateUser(User currentUser, User targetUser) {
        // Staff can update students and some staff members
        return canViewUser(currentUser, targetUser);
    }
    
    @Override
    public boolean canDeleteUser(User currentUser, User targetUser) {
        // Staff can only delete students, not other staff members
        return targetUser.getRole() == UserRole.STUDENT;
    }
    
    @Override
    public boolean validateUserOperation(User currentUser, User targetUser, String operation) {
        if (targetUser.getRole() == UserRole.ADMIN || targetUser.getRole() == UserRole.SUPER_ADMIN) {
            return false; // Cannot manage admins
        }
        
        if ("delete".equals(operation)) {
            // Can only delete students
            return targetUser.getRole() == UserRole.STUDENT;
        }
        
        return true;
    }
    
    @Override
    public String getStrategyName() {
        return "StaffUserManagementStrategy";
    }
}
