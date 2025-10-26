package org.example.se2030helpdesk.controller;

import org.example.se2030helpdesk.model.Booking;
import org.example.se2030helpdesk.model.Event;
import org.example.se2030helpdesk.model.User;
import org.example.se2030helpdesk.service.BookingService;
import org.example.se2030helpdesk.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin
public class BookingController {
    private final  BookingService bookingService;
    private final EventService eventService;

    public BookingController(BookingService bookingService, EventService eventService) {
        this.bookingService = bookingService;
        this.eventService = eventService;
    }

    @GetMapping
    public List<Booking> list(@RequestParam(value = "userId", required = false) Long userId,
                             @RequestParam(value = "eventId", required = false) Long eventId) {
        if (userId != null) {
            return bookingService.findByUserId(userId);
        }
        if (eventId != null) {
            return bookingService.findByEventId(eventId);
        }
        return List.of();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getById(@PathVariable Long id) {
        return bookingService.findByUserId(id).stream()
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Booking> create(@RequestBody BookingRequest request) {
        // Get event and user
        Event event = eventService.findById(request.getEventId()).orElse(null);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }

        // Check if user already booked this event
        if (bookingService.isUserBooked(request.getEventId(), request.getUserId())) {
            return ResponseEntity.badRequest().build();
        }

        // Check if slots are available
        if (event.getAvailableSlots() <= 0) {
            return ResponseEntity.badRequest().build();
        }

        // Create user object (simplified for this example)
        User user = new User();
        user.setId(request.getUserId());

        Booking booking = bookingService.createBooking(event, user);
        return ResponseEntity.ok(booking);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancel(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/event/{eventId}/user/{userId}")
    public ResponseEntity<?> cancelByEventAndUser(@PathVariable Long eventId, @PathVariable Long userId) {
        bookingService.cancelBookingByEventAndUser(eventId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check/{eventId}/{userId}")
    public ResponseEntity<Boolean> isBooked(@PathVariable Long eventId, @PathVariable Long userId) {
        boolean isBooked = bookingService.isUserBooked(eventId, userId);
        return ResponseEntity.ok(isBooked);
    }

    // Inner class for booking request
    public static class BookingRequest {
        private Long eventId;
        private Long userId;

        public Long getEventId() { return eventId; }
        public void setEventId(Long eventId) { this.eventId = eventId; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
    }
}
