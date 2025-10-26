package org.example.se2030helpdesk.service;

import org.example.se2030helpdesk.model.Event;
import org.example.se2030helpdesk.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> findAll() {
        return eventRepository.findByActiveTrueOrderByEventDateAscEventTimeAsc();
    }

    public List<Event> findUpcoming() {
        return eventRepository.findByEventDateGreaterThanEqualOrderByEventDateAscEventTimeAsc(LocalDate.now());
    }

    public List<Event> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return eventRepository.findByEventDateBetweenOrderByEventDateAscEventTimeAsc(startDate, endDate);
    }

    public List<Event> findByDate(LocalDate date) {
        return eventRepository.findEventsByDate(date);
    }

    public List<Event> findByCreatedBy(Long createdById) {
        return eventRepository.findByCreatedByIdAndActiveTrueOrderByEventDateAscEventTimeAsc(createdById);
    }

    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id);
    }

    public Event save(Event event) {
        event.setUpdatedAt(Instant.now());
        return eventRepository.save(event);
    }

    public void deleteById(Long id) {
        Optional<Event> event = eventRepository.findById(id);
        if (event.isPresent()) {
            Event e = event.get();
            e.setActive(false);
            e.setUpdatedAt(Instant.now());
            eventRepository.save(e);
        }
    }

    public boolean hasOverlappingEvents(String hallRoomNumber, LocalDate eventDate, java.time.LocalTime eventTime) {
        List<Event> overlapping = eventRepository.findOverlappingEvents(hallRoomNumber, eventDate, eventTime);
        return !overlapping.isEmpty();
    }
}
