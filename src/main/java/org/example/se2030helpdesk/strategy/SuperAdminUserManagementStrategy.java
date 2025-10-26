package org.example.se2030helpdesk.strategy;

import org.example.se2030helpdesk.model.User;
import org.example.se2030helpdesk.model.UserRole;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class SuperAdminUserManagementStrategy  implements UserManagementStrategy {
    
    @Override
    public boolean canHandle(UserRole role) {
        return role == UserRole.SUPER_ADMIN;
    }
    
    @Override
    public List<User> getManageableUsers(List<User> allUsers, User currentUser) {

        return allUsers;
    }
    
    @Override
    public boolean canViewUser(User currentUser, User targetUser) {

        return true;
    }
    
    @Override
    public boolean canUpdateUser(User currentUser, User targetUser) {

        return true;
    }
    
    @Override
    public boolean canDeleteUser(User currentUser, User targetUser) {

        return !currentUser.getId().equals(targetUser.getId());
    }
    
    @Override
    public boolean validateUserOperation(User currentUser, User targetUser, String operation) {
        if ("delete".equals(operation)) {
            return !currentUser.getId().equals(targetUser.getId());
        }
        return true;
    }
    
    @Override
    public String getStrategyName() {
        return "SuperAdminUserManagementStrategy";
    }
}
