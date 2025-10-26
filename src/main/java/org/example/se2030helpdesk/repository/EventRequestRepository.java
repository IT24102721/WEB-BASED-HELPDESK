package org.example.se2030helpdesk.repository;

import org.example.se2030helpdesk.model.EventRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRequestRepository extends JpaRepository<EventRequest, Long> {
    List<EventRequest> findByStatusOrderByCreatedAtAsc(EventRequest.Status status);
}



