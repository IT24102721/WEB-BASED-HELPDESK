package org.example.se2030helpdesk.controller;

import org.example.se2030helpdesk.model.ResourceRequest;
import org.example.se2030helpdesk.model.User;
import org.example.se2030helpdesk.service.ResourceRequestService;
import org.example.se2030helpdesk.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resource-requests")
@CrossOrigin
public class  ResourceRequestController {
    private final ResourceRequestService requestService;
    private final UserService userService;

    public ResourceRequestController(ResourceRequestService requestService, UserService userService) {
        this.requestService = requestService;
        this.userService = userService;
    }

    @GetMapping
    public List<ResourceRequest> getAllRequests(@RequestParam(required = false) String status) {
        if (status != null) {
            return requestService.findByStatus(ResourceRequest.Status.valueOf(status));
        }
        return requestService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceRequest> getRequestById(@PathVariable Long id) {
        return requestService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public List<ResourceRequest> getRequestsByUser(@PathVariable Long userId) {
        return requestService.findByUserId(userId);
    }

    @GetMapping("/resource/{resourceId}")
    public List<ResourceRequest> getRequestsByResource(@PathVariable Long resourceId) {
        return requestService.findByResourceId(resourceId);
    }

    @PostMapping
    public ResponseEntity<ResourceRequest> submitRequest(@RequestBody ResourceRequest request) {
        ResourceRequest savedRequest = requestService.submitRequest(request);
        return ResponseEntity.ok(savedRequest);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ResourceRequest> approveRequest(
            @PathVariable Long id,
            @RequestParam Long adminId,
            @RequestBody(required = false) Map<String, String> body) {
        User admin = userService.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        String comments = body != null ? body.get("comments") : "";
        ResourceRequest approvedRequest = requestService.approveRequest(id, admin, comments);
        return ResponseEntity.ok(approvedRequest);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ResourceRequest> rejectRequest(
            @PathVariable Long id,
            @RequestParam Long adminId,
            @RequestBody(required = false) Map<String, String> body) {
        User admin = userService.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        String comments = body != null ? body.get("comments") : "";
        ResourceRequest rejectedRequest = requestService.rejectRequest(id, admin, comments);
        return ResponseEntity.ok(rejectedRequest);
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<ResourceRequest> returnResource(@PathVariable Long id) {
        ResourceRequest request = requestService.returnResource(id);
        return ResponseEntity.ok(request);
    }
}
