package org.example.se2030helpdesk.repository;

import org.example.se2030helpdesk.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserIdAndActiveTrueOrderByBookedAtDesc(Long userId);
    List<Booking> findByEventIdAndActiveTrue(Long eventId);
    Optional<Booking> findByEventIdAndUserIdAndActiveTrue(Long eventId, Long userId);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.event.id = :eventId AND b.active = true")
    Long countActiveBookingsByEventId(@Param("eventId") Long eventId);
    
    @Query("SELECT b FROM Booking b WHERE b.event.id = :eventId AND b.active = true ORDER BY b.bookedAt ASC")
    List<Booking> findActiveBookingsByEventId(@Param("eventId") Long eventId);
}
