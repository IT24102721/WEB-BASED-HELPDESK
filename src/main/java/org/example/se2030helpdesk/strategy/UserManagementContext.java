package org.example.se2030helpdesk.strategy;

import org.example.se2030helpdesk.model.User;
import org.example.se2030helpdesk.model.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserManagementContext {
    
    private final List<UserManagementStrategy> strategies;
    
    @Autowired
    public UserManagementContext(List<UserManagementStrategy> strategies) {
        this.strategies = strategies;
    }
    
    
    public UserManagementStrategy getStrategy(UserRole role) {
        return strategies.stream()
                .filter(strategy -> strategy.canHandle(role))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No strategy found for role: " + role));
    }
    
   
    public UserManagementStrategy getStrategy(User user) {
        return getStrategy(user.getRole());
    }
    
    
    public List<User> getManageableUsers(List<User> allUsers, User currentUser) {
        UserManagementStrategy strategy = getStrategy(currentUser);
        return strategy.getManageableUsers(allUsers, currentUser);
    }
    
   
    public boolean canViewUser(User currentUser, User targetUser) {
        UserManagementStrategy strategy = getStrategy(currentUser);
        return strategy.canViewUser(currentUser, targetUser);
    }
    
    
    public boolean canUpdateUser(User currentUser, User targetUser) {
        UserManagementStrategy strategy = getStrategy(currentUser);
        return strategy.canUpdateUser(currentUser, targetUser);
    }
    
  
    public boolean canDeleteUser(User currentUser, User targetUser) {
        UserManagementStrategy strategy = getStrategy(currentUser);
        return strategy.canDeleteUser(currentUser, targetUser);
    }
    
   
    public boolean validateUserOperation(User currentUser, User targetUser, String operation) {
        UserManagementStrategy strategy = getStrategy(currentUser);
        return strategy.validateUserOperation(currentUser, targetUser, operation);
    }
}
