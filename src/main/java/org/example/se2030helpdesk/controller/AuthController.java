package org.example.se2030helpdesk.controller;

import org.example.se2030helpdesk.dto.AuthDtos;
import org.example.se2030helpdesk.model.User;
import org.example.se2030helpdesk.model.UserRole;
import org.example.se2030helpdesk.repository.UserRepository;
import org.example.se2030helpdesk.service.ValidationService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {
    private final UserRepository userRepository;
    private final ValidationService validationService;
    
    public AuthController(UserRepository userRepository, ValidationService validationService) { 
        this.userRepository = userRepository; 
        this.validationService = validationService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthDtos.LoginRequest req){
        if(req == null || req.username == null || req.password == null) return ResponseEntity.badRequest().build();
        return userRepository.findByUsername(req.username)
                .filter(User::isActive)
                .filter(u -> match(u, req.password))
                .<ResponseEntity<?>>map(u -> ResponseEntity.ok(toAuthUser(u)))
                .orElseGet(() -> ResponseEntity.status(401).body(Map.of("error","Invalid credentials")));
    }

    private boolean match(User user, String raw){
        // Demo-only: compare plain text. In production use password hashing.
        return user.getPasswordHash().equals(raw);
    }

    private AuthDtos.AuthUser toAuthUser(User u){
        AuthDtos.AuthUser au = new AuthDtos.AuthUser();
        au.id = u.getId();
        au.username = u.getUsername();
        au.role = u.getRole().name();
        au.active = u.isActive();
        return au;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body){
        String username = body.getOrDefault("username", "");
        String password = body.getOrDefault("password", "");
        String email = body.getOrDefault("email", "");
        String role = body.getOrDefault("role", "STUDENT");
        
        if(username.isBlank() || password.isBlank() || email.isBlank()){
            return ResponseEntity.badRequest().body(Map.of("error","Missing fields"));
        }
        
        // Validate credentials using ValidationService
        ValidationService.ValidationResult validationResult = validationService.validateUserCredentials(email, password);
        if (!validationResult.isValid()) {
            return ResponseEntity.badRequest().body(Map.of("error", validationResult.getErrorMessage()));
        }
        
        if(userRepository.findByUsername(username).isPresent()){
            return ResponseEntity.status(409).body(Map.of("error","Username already exists"));
        }
        
        User u = new User();
        u.setUsername(username);
        u.setEmail(email.trim()); // Ensure we store the trimmed email
        u.setPasswordHash(password);
        try{ u.setRole(UserRole.valueOf(role)); }catch(Exception ex){ u.setRole(UserRole.STUDENT); }
        u.setActive(true);
        try{
            userRepository.save(u);
            return ResponseEntity.ok(Map.of("ok",true));
        }catch(DataIntegrityViolationException e){
            return ResponseEntity.status(409).body(Map.of("error","Email already exists"));
        }
    }
}


