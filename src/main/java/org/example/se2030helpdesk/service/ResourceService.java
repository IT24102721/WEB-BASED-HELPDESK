package org.example.se2030helpdesk.service;

import org.example.se2030helpdesk.model.Resource;
import org.example.se2030helpdesk.repository.ResourceRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ResourceService {
    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public List<Resource> findAll() {
        return resourceRepository.findAll();
    }

    public Optional<Resource> findById(Long id) {
        return resourceRepository.findById(id);
    }

    public List<Resource> findByStatus(Resource.Status status) {
        return resourceRepository.findByStatus(status);
    }

    public List<Resource> findByCategory(String category) {
        return resourceRepository.findByCategory(category);
    }

    public List<Resource> findByLocation(String location) {
        return resourceRepository.findByLocation(location);
    }

    public List<Resource> findByCategoryAndStatus(String category, Resource.Status status) {
        return resourceRepository.findByCategoryAndStatus(category, status);
    }

    public List<Resource> searchResources(String search) {
        return resourceRepository.searchResources(search);
    }

    public List<String> getDistinctCategories() {
        return resourceRepository.findDistinctCategories();
    }

    public List<String> getDistinctLocations() {
        return resourceRepository.findDistinctLocations();
    }

    public Resource save(Resource resource) {
        resource.setUpdatedAt(Instant.now());
        
        // Handle empty serial number
        if (resource.getSerialNumber() != null && resource.getSerialNumber().trim().isEmpty()) {
            resource.setSerialNumber(null);
        }
        
        return resourceRepository.save(resource);
    }

    public void deleteById(Long id) {
        resourceRepository.deleteById(id);
    }

    public Resource updateStatus(Long id, Resource.Status status) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found"));
        resource.setStatus(status);
        resource.setUpdatedAt(Instant.now());
        return resourceRepository.save(resource);
    }
}
