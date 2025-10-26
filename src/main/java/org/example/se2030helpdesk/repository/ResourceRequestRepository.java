package org.example.se2030helpdesk.repository;

import org.example.se2030helpdesk.model.ResourceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRequestRepository extends JpaRepository<ResourceRequest, Long> {
    List<ResourceRequest> findByStatus(ResourceRequest.Status status);
    List<ResourceRequest> findByUserIdOrderByRequestDateDesc(Long userId);
    List<ResourceRequest> findByResourceIdOrderByRequestDateDesc(Long resourceId);
    List<ResourceRequest> findByStatusOrderByRequestDateAsc(ResourceRequest.Status status);
    List<ResourceRequest> findByResourceIdAndStatus(Long resourceId, ResourceRequest.Status status);
}
