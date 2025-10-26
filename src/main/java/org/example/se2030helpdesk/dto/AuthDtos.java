package org.example.se2030helpdesk.dto;

public class  AuthDtos {
    public static class LoginRequest {
        public  String username;
        public String password;
    }
    public static class AuthUser {
        public Long id;
        public String username;
        public String role;
        public boolean active;
    }
}


