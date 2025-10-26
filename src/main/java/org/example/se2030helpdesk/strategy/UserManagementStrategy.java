package org.example.se2030helpdesk.strategy;

import org.example.se2030helpdesk.model.User;
import org.example.se2030helpdesk.model.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserManagementStrategy {
    

    boolean canHandle(UserRole role);
    
   
    List<User> getManageableUsers(List<User> allUsers, User currentUser);
    
    
    boolean canViewUser(User currentUser, User targetUser);
    
    
    boolean canUpdateUser(User currentUser, User targetUser);
    
    
    boolean canDeleteUser(User currentUser, User targetUser);
    
    
    boolean validateUserOperation(User currentUser, User targetUser, String operation);
    
   
    String getStrategyName();
}
