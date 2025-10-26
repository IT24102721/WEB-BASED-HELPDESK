package org.example.se2030helpdesk.controller;

import org.example.se2030helpdesk.model.Resource;
import org.example.se2030helpdesk.service.ResourceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resources")
@CrossOrigin
public class ResourceController {
    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping
    public List<Resource> getAllResources(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String search) {
        
        if (search != null && !search.trim().isEmpty()) {
            return resourceService.searchResources(search.trim());
        }
        
        if (category != null && status != null) {
            return resourceService.findByCategoryAndStatus(category, Resource.Status.valueOf(status));
        }
        
        if (category != null) {
            return resourceService.findByCategory(category);
        }
        
        if (status != null) {
            return resourceService.findByStatus(Resource.Status.valueOf(status));
        }
        
        if (location != null) {
            return resourceService.findByLocation(location);
        }
        
        return resourceService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> getResourceById(@PathVariable Long id) {
        return resourceService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categories")
    public List<String> getCategories() {
        return resourceService.getDistinctCategories();
    }

    @GetMapping("/locations")
    public List<String> getLocations() {
        return resourceService.getDistinctLocations();
    }

    @PostMapping
    public ResponseEntity<Resource> createResource(@RequestBody Resource resource) {
        Resource savedResource = resourceService.save(resource);
        return ResponseEntity.ok(savedResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Resource> updateResource(@PathVariable Long id, @RequestBody Resource resource) {
        return resourceService.findById(id)
                .map(existingResource -> {
                    resource.setId(id);
                    resource.setCreatedAt(existingResource.getCreatedAt());
                    Resource updatedResource = resourceService.save(resource);
                    return ResponseEntity.ok(updatedResource);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Resource> updateResourceStatus(@PathVariable Long id, @RequestBody Map<String, String> statusUpdate) {
        try {
            Resource.Status status = Resource.Status.valueOf(statusUpdate.get("status"));
            Resource updatedResource = resourceService.updateStatus(id,  status);
            return ResponseEntity.ok(updatedResource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteResource(@PathVariable Long id) {
        try {
            resourceService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
