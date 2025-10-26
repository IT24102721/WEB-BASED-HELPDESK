package org.example.se2030helpdesk.service;

import org.example.se2030helpdesk.model.Event;
import org.example.se2030helpdesk.model.EventRequest;
import org.example.se2030helpdesk.model.User;
import org.example.se2030helpdesk.repository.EventRequestRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class  EventRequestService {
    private final EventRequestRepository repo;
    private final EventService eventService;

    public EventRequestService(EventRequestRepository repo, EventService eventService) {
        this.repo = repo;
        this.eventService = eventService;
    }

    public List<EventRequest> listPending() {
        return repo.findByStatusOrderByCreatedAtAsc(EventRequest.Status.PENDING);
    }

    public EventRequest submit(EventRequest request) {
        request.setStatus(EventRequest.Status.PENDING);
        request.setUpdatedAt(Instant.now());
        return repo.save(request);
    }

    public Optional<EventRequest> findById(Long id) { return repo.findById(id); }

    public Event approveAndCreateEvent(Long id, User admin) {
        EventRequest req = repo.findById(id).orElseThrow();
        
        req.setStatus(EventRequest.Status.APPROVED);
        req.setUpdatedAt(Instant.now());
        repo.save(req);

        Event event = new Event();
        event.setEventName(req.getTitle());
        event.setEventDate(req.getProposedDate());
        event.setEventTime(req.getProposedTime());
        event.setHallRoomNumber(req.getHallRoomNumber());
        event.setCapacity(req.getCapacity());
        event.setAvailableSlots(req.getCapacity());
        event.setDescription(req.getDescription());
        event.setCreatedBy(admin);  // Set the admin user who approved it
        return eventService.save(event);
    }

    public EventRequest reject(Long id) {
        EventRequest req = repo.findById(id).orElseThrow();
        req.setStatus(EventRequest.Status.REJECTED);
        req.setUpdatedAt(Instant.now());
        return repo.save(req);
    }
}



