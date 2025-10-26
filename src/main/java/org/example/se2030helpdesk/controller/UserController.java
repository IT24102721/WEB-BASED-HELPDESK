package org.example.se2030helpdesk.controller;

import org.example.se2030helpdesk.model.User;
import org.example.se2030helpdesk.model.UserRole;
import org.example.se2030helpdesk.service.UserService;
import org.example.se2030helpdesk.service.EmailService;
import org.example.se2030helpdesk.service.AuditService;
import org.example.se2030helpdesk.service.ValidationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {
    private final UserService userService;
    private final EmailService emailService;
    private final AuditService auditService;
    private final ValidationService validationService;
    
    public UserController(UserService userService, EmailService emailService, AuditService auditService, ValidationService validationService){ 
        this.userService = userService; 
        this.emailService = emailService;
        this.auditService = auditService;
        this.validationService = validationService;
    }

    @GetMapping
    public ResponseEntity<?> all(@RequestParam(name = "role", required = false) UserRole role,
                                 @RequestParam(name = "currentUserId", required = false) Long currentUserId){
        if(role != null){
            return ResponseEntity.ok(userService.findByRole(role));
        }
        
        // If currentUserId is provided, return only users that the current user can manage
        if(currentUserId != null) {
            Optional<User> currentUser = userService.findById(currentUserId);
            if(currentUser.isPresent()) {
                return ResponseEntity.ok(userService.getManageableUsers(currentUser.get()));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found with ID: " + currentUserId));
            }
        }
        
        // Return error message to enforce strategy pattern usage
        return ResponseEntity.badRequest().body(Map.of("error", "currentUserId parameter is required to use role-based access control"));
    }

    @GetMapping("/debug")
    public ResponseEntity<?> debug() {
        try {
            List<User> allUsers = userService.findAll();
            return ResponseEntity.ok(Map.of(
                "message", "All users in database",
                "totalUsers", allUsers.size(),
                "users", allUsers.stream().map(user -> Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "role", user.getRole(),
                    "active", user.isActive()
                )).toList()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/test-strategy")
    public ResponseEntity<?> testStrategy(@RequestParam Long currentUserId) {
        try {
            Optional<User> currentUser = userService.findById(currentUserId);
            if(currentUser.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found with ID: " + currentUserId));
            }
            
            List<User> allUsers = userService.findAll();
            List<User> manageableUsers = userService.getManageableUsers(currentUser.get());
            
            return ResponseEntity.ok(Map.of(
                "currentUser", Map.of(
                    "id", currentUser.get().getId(),
                    "username", currentUser.get().getUsername(),
                    "role", currentUser.get().getRole()
                ),
                "totalUsersInDatabase", allUsers.size(),
                "manageableUsers", manageableUsers.size(),
                "manageableUsersList", manageableUsers.stream().map(user -> Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "role", user.getRole()
                )).toList(),
                "strategyWorking", true
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody User incoming,
                                   @RequestParam(name = "currentUserId", required = false) Long currentUserId){
        return userService.findById(id)
                .map(u -> {
                    // Check if current user can update this user
                    if(currentUserId != null) {
                        Optional<User> currentUser = userService.findById(currentUserId);
                        if(currentUser.isPresent() && !userService.canUpdateUser(currentUser.get(), u)) {
                            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
                        }
                    }
                    String oldRole = u.getRole().name();
                    String oldEmail = u.getEmail();
                    
                    // Validate email if it's being updated
                    if(incoming.getEmail() != null && !incoming.getEmail().equals(oldEmail)) {
                        ValidationService.ValidationResult emailValidation = validationService.validateEmail(incoming.getEmail());
                        if (!emailValidation.isValid()) {
                            return ResponseEntity.badRequest().body(Map.of("error", emailValidation.getErrorMessage()));
                        }
                    }
                    
                    // Validate password if it's being updated
                    if(incoming.getPasswordHash() != null && !incoming.getPasswordHash().isBlank()) {
                        ValidationService.ValidationResult passwordValidation = validationService.validatePassword(incoming.getPasswordHash());
                        if (!passwordValidation.isValid()) {
                            return ResponseEntity.badRequest().body(Map.of("error", passwordValidation.getErrorMessage()));
                        }
                    }
                    
                    u.setUsername(incoming.getUsername()!=null?incoming.getUsername():u.getUsername());
                    u.setEmail(incoming.getEmail()!=null?incoming.getEmail().trim():u.getEmail());
                    if(incoming.getRole()!=null) u.setRole(incoming.getRole());
                    u.setActive(incoming.isActive());
                    if(incoming.getPasswordHash()!=null && !incoming.getPasswordHash().isBlank()) u.setPasswordHash(incoming.getPasswordHash());
                    
                    User savedUser = userService.save(u);
                    
                    // Send email notifications
                    if(incoming.getEmail()!=null && !incoming.getEmail().equals(oldEmail)) {
                        emailService.sendProfileUpdateNotification(savedUser.getEmail(), savedUser.getUsername());
                    }
                    if(incoming.getRole()!=null && !incoming.getRole().name().equals(oldRole)) {
                        emailService.sendRoleChangeNotification(savedUser.getEmail(), savedUser.getUsername(), oldRole, incoming.getRole().name());
                    }
                    if(incoming.getPasswordHash()!=null && !incoming.getPasswordHash().isBlank()) {
                        emailService.sendPasswordResetNotification(savedUser.getEmail(), savedUser.getUsername());
                    }
                    
                    // Log audit trail
                    auditService.logAction("USER_UPDATED", "admin", savedUser.getUsername(), 
                        "Updated user profile: " + (incoming.getRole()!=null ? "role="+incoming.getRole().name() : "") +
                        (incoming.getEmail()!=null ? " email="+incoming.getEmail() : "") +
                        (incoming.getPasswordHash()!=null ? " password=changed" : ""), "127.0.0.1");
                    
                    return ResponseEntity.ok(savedUser);
                }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public User create(@RequestBody User user){ return userService.save(user); }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, 
                                   @RequestParam(name = "currentUserId", required = false) Long currentUserId){ 
        Optional<User> user = userService.findById(id);
        if(user.isPresent()) {
            // Check if current user can delete this user
            if(currentUserId != null) {
                Optional<User> currentUser = userService.findById(currentUserId);
                if(currentUser.isPresent() && !userService.canDeleteUser(currentUser.get(), user.get())) {
                    return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
                }
            }
            
            auditService.logAction("USER_DELETED", "admin", user.get().getUsername(), 
                "User account deleted", "127.0.0.1");
        }
        userService.deleteById(id); 
        return ResponseEntity.noContent().build(); 
    }
}