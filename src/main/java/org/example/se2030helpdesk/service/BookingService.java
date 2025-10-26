package org.example.se2030helpdesk.service;

import org.example.se2030helpdesk.model.Booking;
import org.example.se2030helpdesk.model.Event;
import org.example.se2030helpdesk.model.User;
import org.example.se2030helpdesk.repository.BookingRepository;
import org.example.se2030helpdesk.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;

    public BookingService(BookingRepository bookingRepository, EventRepository eventRepository) {
        this.bookingRepository =  bookingRepository;
        this.eventRepository = eventRepository;
    }

    public List<Booking> findByUserId(Long userId) {
        return bookingRepository.findByUserIdAndActiveTrueOrderByBookedAtDesc(userId);
    }

    public List<Booking> findByEventId(Long eventId) {
        return bookingRepository.findByEventIdAndActiveTrue(eventId);
    }

    public Optional<Booking> findByEventIdAndUserId(Long eventId, Long userId) {
        return bookingRepository.findByEventIdAndUserIdAndActiveTrue(eventId, userId);
    }

    public Booking createBooking(Event event, User user) {
        Booking booking = new Booking();
        booking.setEvent(event);
        booking.setUser(user);
        booking.setActive(true);
        
        Booking savedBooking = bookingRepository.save(booking);
        
        // Update available slots
        updateEventSlots(event.getId());
        
        return savedBooking;
    }

    public void cancelBooking(Long bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isPresent()) {
            Booking b = booking.get();
            b.setActive(false);
            bookingRepository.save(b);
            
            // Update available slots
            updateEventSlots(b.getEvent().getId());
        }
    }

    public void cancelBookingByEventAndUser(Long eventId, Long userId) {
        Optional<Booking> booking = bookingRepository.findByEventIdAndUserIdAndActiveTrue(eventId, userId);
        if (booking.isPresent()) {
            cancelBooking(booking.get().getId());
        }
    }

    public boolean isUserBooked(Long eventId, Long userId) {
        return bookingRepository.findByEventIdAndUserIdAndActiveTrue(eventId, userId).isPresent();
    }

    public Long getBookingCount(Long eventId) {
        return bookingRepository.countActiveBookingsByEventId(eventId);
    }

    private void updateEventSlots(Long eventId) {
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isPresent()) {
            Event event = eventOpt.get();
            Long bookingCount = bookingRepository.countActiveBookingsByEventId(eventId);
            event.setAvailableSlots(event.getCapacity() - bookingCount.intValue());
            eventRepository.save(event);
        }
    }
}
