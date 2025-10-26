package org.example.se2030helpdesk.controller;

import org.example.se2030helpdesk.model.Event;
import org.example.se2030helpdesk.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
@CrossOrigin
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<Event> list(@RequestParam(value = "upcoming", required = false) Boolean upcoming,
                           @RequestParam(value = "startDate", required = false) String startDate,
                           @RequestParam(value = "endDate", required = false) String endDate,
                           @RequestParam(value = "date", required = false) String date,
                           @RequestParam(value = "createdBy", required = false) Long createdBy) {
        
        if (upcoming != null && upcoming) {
            return eventService.findUpcoming();
        }
        
        if (startDate != null && endDate != null) {
            return eventService.findByDateRange(LocalDate.parse(startDate), LocalDate.parse(endDate));
        }
        
        if (date != null) {
            return eventService.findByDate(LocalDate.parse(date));
        }
        
        if (createdBy != null) {
            return eventService.findByCreatedBy(createdBy);
        }
        
        return eventService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getById(@PathVariable Long id) {
        return eventService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Event event) {
        try {
            System.out.println("Creating event: " + event.getEventName());
            System.out.println("Event date: " + event.getEventDate());
            System.out.println("Event time: " + event.getEventTime());
            System.out.println("Hall: " + event.getHallRoomNumber());
            System.out.println("Capacity: " + event.getCapacity());
            
            // Set initial available slots equal to capacity
            event.setAvailableSlots(event.getCapacity());
            
            Event savedEvent = eventService.save(event);
            System.out.println("Event created successfully with ID: " + savedEvent.getId());
            return ResponseEntity.ok(savedEvent);
        } catch (RuntimeException e) {
            System.err.println("Event validation failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage(),
                "message", "Event validation failed",
                "details", e.getMessage()
            ));
        } catch (Exception e) {
            System.err.println("Error creating event: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "error", "An unexpected error occurred",
                "message", "Failed to create event",
                "details", e.getMessage()
            ));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Event incoming) {
        try {
            return eventService.findById(id)
                    .map(event -> {
                        if (incoming.getEventName() != null) event.setEventName(incoming.getEventName());
                        if (incoming.getEventDate() != null) event.setEventDate(incoming.getEventDate());
                        if (incoming.getEventTime() != null) event.setEventTime(incoming.getEventTime());
                        if (incoming.getHallRoomNumber() != null) event.setHallRoomNumber(incoming.getHallRoomNumber());
                        if (incoming.getCapacity() != null) {
                            event.setCapacity(incoming.getCapacity());
                            // Recalculate available slots
                            event.setAvailableSlots(incoming.getCapacity() - (event.getCapacity() - event.getAvailableSlots()));
                        }
                        if (incoming.getDescription() != null) event.setDescription(incoming.getDescription());
                        
                        return ResponseEntity.ok(eventService.save(event));
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            System.err.println("Event validation failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage(),
                "message", "Event validation failed",
                "details", e.getMessage()
            ));
        } catch (Exception e) {
            System.err.println("Error updating event: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "error", "An unexpected error occurred",
                "message", "Failed to update event",
                "details", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        eventService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
