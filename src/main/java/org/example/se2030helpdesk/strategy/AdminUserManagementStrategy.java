package org.example.se2030helpdesk.strategy;

import org.example.se2030helpdesk.model.User;
import org.example.se2030helpdesk.model.UserRole;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
public class  AdminUserManagementStrategy implements UserManagementStrategy {
    
    @Override
    public boolean canHandle(UserRole role) {
        return role == UserRole.ADMIN;
    }
    
    @Override
    public List<User> getManageableUsers(List<User> allUsers, User currentUser) {
        // Admins can manage all users except super admins
        return allUsers.stream()
                .filter(user -> user.getRole() != UserRole.SUPER_ADMIN)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean canViewUser(User currentUser, User targetUser) {
        // Admins can view all users except super admins
        return targetUser.getRole() != UserRole.SUPER_ADMIN;
    }
    
    @Override
    public boolean canUpdateUser(User currentUser, User targetUser) {
        // Admins can update all users except super admins
        return targetUser.getRole() != UserRole.SUPER_ADMIN;
    }
    
    @Override
    public boolean canDeleteUser(User currentUser, User targetUser) {
        // Admins can delete users except super admins and other admins
        return targetUser.getRole() != UserRole.SUPER_ADMIN && 
               targetUser.getRole() != UserRole.ADMIN;
    }
    
    @Override
    public boolean validateUserOperation(User currentUser, User targetUser, String operation) {
        if (targetUser.getRole() == UserRole.SUPER_ADMIN) {
            return false; // Cannot manage super admins
        }
        
        if ("delete".equals(operation)) {
            return targetUser.getRole() != UserRole.ADMIN; // Cannot delete other admins
        }
        
        return true;
    }
    
    @Override
    public String getStrategyName() {
        return "AdminUserManagementStrategy";
    }
}
