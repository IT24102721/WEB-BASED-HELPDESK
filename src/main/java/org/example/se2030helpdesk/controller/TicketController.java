package org.example.se2030helpdesk.controller;

import org.example.se2030helpdesk.dto.TicketDto;
import org.example.se2030helpdesk.model.Ticket;
import org.example.se2030helpdesk.model.TicketStatus;
import org.example.se2030helpdesk.model.User;
import org.example.se2030helpdesk.repository.UserRepository;
import org.example.se2030helpdesk.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin
public class TicketController {
    private final TicketService ticketService;
    private final UserRepository userRepository;
    
    public TicketController(TicketService ticketService, UserRepository userRepository){ 
        this.ticketService = ticketService; 
        this.userRepository = userRepository; 
    }

    @GetMapping
    public List<Ticket> list(@RequestParam(value="studentId", required=false) Long studentId,
                             @RequestParam(value="status", required=false) TicketStatus status){
        try {
            System.out.println("TicketController.list() called with studentId=" + studentId + ", status=" + status);
            
            // If studentId is provided, return only that student's tickets
            if(studentId != null){ 
                Optional<User> u = userRepository.findById(studentId); 
                List<Ticket> result = u.map(ticketService::findByStudent).orElse(List.of());
                System.out.println("Found " + result.size() + " tickets for student " + studentId);
                return result;
            }
            // If status filter is provided, return tickets with that status
            if(status != null){ 
                List<Ticket> result = ticketService.findByStatus(status);
                System.out.println("Found " + result.size() + " tickets with status " + status);
                return result;
            }
            // Return all tickets (for admin access)
            List<Ticket> result = ticketService.findAll();
            System.out.println("Found " + result.size() + " total tickets");
            for (Ticket ticket : result) {
                System.out.println("Ticket ID: " + ticket.getId() + 
                    ", Title: " + ticket.getTitle() + 
                    ", Student: " + (ticket.getStudent() != null ? ticket.getStudent().getUsername() : "NULL") +
                    ", Status: " + ticket.getStatus() +
                    ", Category: " + ticket.getCategory());
            }
            return result;
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("No enum constant") && e.getMessage().contains("TicketStatus")) {
                System.err.println("ENUM MISMATCH ERROR: Database contains invalid ticket status values!");
                System.err.println("Please run the fix-ticket-status.sql script to update the database.");
                System.err.println("Error details: " + e.getMessage());
                return List.of();
            }
            System.err.println("Error in TicketController.list(): " + e.getMessage());
            e.printStackTrace();
            return List.of();
        } catch (Exception e) {
            System.err.println("Error in TicketController.list(): " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody TicketDto ticketDto){
        try {
            System.out.println("Creating ticket with data: " + ticketDto);
            
            if(ticketDto.getStudentId() == null) {
                System.out.println("Error: StudentId is null");
                return ResponseEntity.badRequest().body(Map.of("error", "Student ID must be specified"));
            }
            
            Optional<User> student = userRepository.findById(ticketDto.getStudentId());
            if(student.isEmpty()) {
                System.out.println("Error: Student not found with ID: " + ticketDto.getStudentId());
                return ResponseEntity.badRequest().body(Map.of("error", "Student not found with ID: " + ticketDto.getStudentId()));
            }
            
            System.out.println("Found student: " + student.get().getUsername());
            
            Ticket ticket = new Ticket();
            ticket.setTitle(ticketDto.getTitle());
            ticket.setDescription(ticketDto.getDescription());
            ticket.setCategory(ticketDto.getCategory());
            ticket.setStudent(student.get());
            ticket.setStatus(TicketStatus.PENDING);
            
            System.out.println("Saving ticket: " + ticket.getTitle());
            Ticket savedTicket = ticketService.save(ticket);
            System.out.println("Ticket saved with ID: " + savedTicket.getId());
            
            return ResponseEntity.ok(savedTicket);
        } catch (RuntimeException e) {
            System.err.println("Ticket validation failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error creating ticket: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Ticket incoming){
        try {
            return ticketService.findById(id).map(t -> {
                if(incoming.getTitle()!=null) t.setTitle(incoming.getTitle());
                if(incoming.getDescription()!=null) t.setDescription(incoming.getDescription());
                if(incoming.getCategory()!=null) t.setCategory(incoming.getCategory());
                if(incoming.getStatus()!=null) t.setStatus(incoming.getStatus());
                if(incoming.getAssignedTo()!=null) t.setAssignedTo(incoming.getAssignedTo());
                t.setUpdatedAt(java.time.Instant.now());
                return ResponseEntity.ok(ticketService.save(t));
            }).orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "An unexpected error occurred"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){ ticketService.deleteById(id); return ResponseEntity.noContent().build(); }
    
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        List<String> categories = List.of("IT", "Academics", "Other");
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        try {
            System.out.println("Testing database connection...");
            List<Ticket> tickets = ticketService.findAll();
            
            // Also check users
            List<User> users = userRepository.findAll();
            System.out.println("Total users: " + users.size());
            for (User user : users) {
                System.out.println("User ID: " + user.getId() + ", Username: " + user.getUsername() + ", Role: " + user.getRole());
            }
            
            return ResponseEntity.ok(Map.of(
                "message", "Database connection successful",
                "ticketCount", tickets.size(),
                "userCount", users.size(),
                "tickets", tickets,
                "users", users
            ));
        } catch (Exception e) {
            System.err.println("Database test failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                "error", "Database connection failed",
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/simple-test")
    public ResponseEntity<?> simpleTest() {
        System.out.println("Simple test endpoint called");
        return ResponseEntity.ok(Map.of(
            "message", "Simple test successful",
            "timestamp", System.currentTimeMillis()
        ));
    }
    
    @GetMapping("/debug")
    public ResponseEntity<?> debug() {
        System.out.println("Debug endpoint called - returning mock tickets");
        List<Map<String, Object>> mockTickets = List.of(
            Map.of(
                "id", 1L,
                "title", "Test Ticket 1",
                "description", "This is a test ticket",
                "category", "IT",
                "status", "PENDING",
                "student", Map.of("id", 1L, "username", "testuser", "email", "test@example.com"),
                "createdAt", "2024-01-01T00:00:00Z"
            ),
            Map.of(
                "id", 2L,
                "title", "Test Ticket 2", 
                "description", "Another test ticket",
                "category", "Academics",
                "status", "IN_PROGRESS",
                "student", Map.of("id", 2L, "username", "testuser2", "email", "test2@example.com"),
                "createdAt", "2024-01-02T00:00:00Z"
            )
        );
        return ResponseEntity.ok(mockTickets);
    }
    
    @PostMapping("/test-create")
    public ResponseEntity<?> testCreate() {
        try {
            System.out.println("Creating test ticket...");
            
            // Find first student user
            List<User> users = userRepository.findAll();
            User student = users.stream()
                .filter(u -> u.getRole().name().equals("STUDENT"))
                .findFirst()
                .orElse(null);
                
            if (student == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "No student users found"));
            }
            
            Ticket testTicket = new Ticket();
            testTicket.setTitle("Test Ticket from Admin");
            testTicket.setDescription("This is a test ticket created by admin");
            testTicket.setCategory("IT");
            testTicket.setStudent(student);
            testTicket.setStatus(TicketStatus.PENDING);
            
            Ticket savedTicket = ticketService.save(testTicket);
            System.out.println("Test ticket created with ID: " + savedTicket.getId());
            
            return ResponseEntity.ok(Map.of(
                "message", "Test ticket created successfully",
                "ticketId", savedTicket.getId(),
                "ticket", savedTicket
            ));
        } catch (Exception e) {
            System.err.println("Test ticket creation failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                "error", "Test ticket creation failed",
                "message", e.getMessage()
            ));
        }
    }
}


