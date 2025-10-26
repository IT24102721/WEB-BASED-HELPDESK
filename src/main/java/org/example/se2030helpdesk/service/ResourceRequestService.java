package org.example.se2030helpdesk.service;

import org.example.se2030helpdesk.model.Resource;
import org.example.se2030helpdesk.model.ResourceRequest;
import org.example.se2030helpdesk.model.User;
import org.example.se2030helpdesk.repository.ResourceRequestRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class  ResourceRequestService {
    private final ResourceRequestRepository requestRepository;
    private final ResourceService resourceService;

    public ResourceRequestService(ResourceRequestRepository requestRepository, 
                                ResourceService resourceService) {
        this.requestRepository = requestRepository;
        this.resourceService = resourceService;
    }

    public List<ResourceRequest> findAll() {
        return requestRepository.findAll();
    }

    public Optional<ResourceRequest> findById(Long id) {
        return requestRepository.findById(id);
    }

    public List<ResourceRequest> findByStatus(ResourceRequest.Status status) {
        return requestRepository.findByStatusOrderByRequestDateAsc(status);
    }

    public List<ResourceRequest> findByUserId(Long userId) {
        return requestRepository.findByUserIdOrderByRequestDateDesc(userId);
    }

    public List<ResourceRequest> findByResourceId(Long resourceId) {
        return requestRepository.findByResourceIdOrderByRequestDateDesc(resourceId);
    }

    public ResourceRequest submitRequest(ResourceRequest request) {
        request.setStatus(ResourceRequest.Status.PENDING);
        return requestRepository.save(request);
    }

    public ResourceRequest approveRequest(Long requestId, User admin, String comments) {
        ResourceRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        
        request.setStatus(ResourceRequest.Status.APPROVED);
        request.setAdmin(admin);
        request.setAdminComments(comments);
        request.setApprovedDate(Instant.now());
        
        // Update resource status to IN_USE
        resourceService.updateStatus(request.getResource().getId(), Resource.Status.IN_USE);
        
        return requestRepository.save(request);
    }

    public ResourceRequest rejectRequest(Long requestId, User admin, String comments) {
        ResourceRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        
        request.setStatus(ResourceRequest.Status.REJECTED);
        request.setAdmin(admin);
        request.setAdminComments(comments);
        request.setApprovedDate(Instant.now());
        
        return requestRepository.save(request);
    }

    public ResourceRequest returnResource(Long requestId) {
        ResourceRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        
        if (request.getStatus() == ResourceRequest.Status.APPROVED) {
            // Update resource status back to AVAILABLE
            resourceService.updateStatus(request.getResource().getId(), Resource.Status.AVAILABLE);
        }
        
        return request;
    }
}
