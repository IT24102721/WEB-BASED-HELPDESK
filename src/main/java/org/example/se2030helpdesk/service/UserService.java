package org.example.se2030helpdesk.service;

import org.example.se2030helpdesk.model.User;
import org.example.se2030helpdesk.model.UserRole;
import org.example.se2030helpdesk.repository.UserRepository;
import org.example.se2030helpdesk.strategy.UserManagementContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserManagementContext userManagementContext;

    public UserService(UserRepository userRepository, UserManagementContext userManagementContext) {
        this.userRepository = userRepository;
        this.userManagementContext = userManagementContext;
    }

    public List<User> findAll() { 
        return userRepository.findAll(); 
    }

    public Optional<User> findById(Long id) { 
        return userRepository.findById(id); 
    }

    public Optional<User> findByUsername(String username) { 
        return userRepository.findByUsername(username); 
    }

    public User save(User user) { 
        return userRepository.save(user); 
    }

    public void deleteById(Long id) { 
        userRepository.deleteById(id); 
    }

    public List<User> findByRole(UserRole role) { 
        return userRepository.findByRole(role); 
    }
    
    /**
     * Get users that the current user can manage based on their role
     */
    public List<User> getManageableUsers(User currentUser) {
        List<User> allUsers = findAll();
        return userManagementContext.getManageableUsers(allUsers, currentUser);
    }
    
    /**
     * Check if the current user can view a specific user
     */
    public boolean canViewUser(User currentUser, User targetUser) {
        return userManagementContext.canViewUser(currentUser, targetUser);
    }
    
    /**
     * Check if the current user can update a specific user
     */
    public boolean canUpdateUser(User currentUser, User targetUser) {
        return userManagementContext.canUpdateUser(currentUser, targetUser);
    }
    
    /**
     * Check if the current user can delete a specific user
     */
    public boolean canDeleteUser(User currentUser, User targetUser) {
        return userManagementContext.canDeleteUser(currentUser, targetUser);
    }
    
    /**
     * Validate a user operation based on role-specific rules
     */
    public boolean validateUserOperation(User currentUser, User targetUser, String operation) {
        return userManagementContext.validateUserOperation(currentUser, targetUser, operation);
    }
}


