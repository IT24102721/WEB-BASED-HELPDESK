package org.example.se2030helpdesk.strategy;

import org.example.se2030helpdesk.model.User;
import org.example.se2030helpdesk.model.UserRole;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class StudentUserManagementStrategy implements UserManagementStrategy {
    
    @Override
    public boolean canHandle(UserRole role) {
        return role == UserRole.STUDENT;
    }
    
    @Override
    public List<User> getManageableUsers(List<User> allUsers, User currentUser) {
        // Students can only see their own profile
        return Collections.singletonList(currentUser);
    }
    
    @Override
    public boolean canViewUser(User currentUser, User targetUser) {
        // Students can only view their own profile
        return currentUser.getId().equals(targetUser.getId());
    }
    
    @Override
    public boolean canUpdateUser(User currentUser, User targetUser) {
        // Students can only update their own profile (limited fields)
        return currentUser.getId().equals(targetUser.getId());
    }
    
    @Override
    public boolean canDeleteUser(User currentUser, User targetUser) {
        // Students cannot delete any users
        return false;
    }
    
    @Override
    public boolean validateUserOperation(User currentUser, User targetUser, String operation) {
        if (currentUser.getId().equals(targetUser.getId())) {
            // Students can only update their own basic info, not role or status
            if ("update".equals(operation)) {
                return targetUser.getRole() == UserRole.STUDENT && targetUser.isActive();
            }
        }
        return false;
    }
    
    @Override
    public String getStrategyName() {
        return "StudentUserManagementStrategy";
    }
}
