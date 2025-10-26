package org.example.se2030helpdesk.controller;

import org.example.se2030helpdesk.model.Event;
import org.example.se2030helpdesk.model.EventRequest;
import org.example.se2030helpdesk.model.User;
import org.example.se2030helpdesk.service.EventRequestService;
import org.example.se2030helpdesk.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/event-requests")
@CrossOrigin
public class EventRequestController {
    private final EventRequestService service;
    private final UserService userService;

    public EventRequestController(EventRequestService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    @GetMapping
    public List<EventRequest> listPending() {
        return service.listPending();
    }

    @PostMapping
    public ResponseEntity<?> submit(@RequestBody EventRequest request) {
        try {
            return ResponseEntity.ok(service.submit(request));
        } catch (RuntimeException e) {
            System.err.println("Event request validation failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage(),
                "message", "Event request validation failed",
                "details", e.getMessage()
            ));
        } catch (Exception e) {
            System.err.println("Error submitting event request: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "error", "An unexpected error occurred",
                "message", "Failed to submit event request",
                "details", e.getMessage()
            ));
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Long id, @RequestParam("adminId") Long adminId) {
        try {
            User admin = userService.findById(adminId).orElseThrow(() -> new RuntimeException("Admin not found"));
            Event created = service.approveAndCreateEvent(id, admin);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            System.err.println("Event approval validation failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage(),
                "message", "Event approval validation failed",
                "details", e.getMessage()
            ));
        } catch (Exception e) {
            System.err.println("Error approving event request: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "error", "An unexpected error occurred",
                "message", "Failed to approve event request",
                "details", e.getMessage()
            ));
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.reject(id));
        } catch (RuntimeException e) {
            System.err.println("Event rejection failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage(),
                "message", "Event rejection failed",
                "details", e.getMessage()
            ));
        } catch (Exception e) {
            System.err.println("Error rejecting event request: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "error", "An unexpected error occurred",
                "message", "Failed to reject event request",
                "details", e.getMessage()
            ));
        }
    }
}


