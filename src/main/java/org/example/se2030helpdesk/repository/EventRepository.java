package org.example.se2030helpdesk.repository;

import org.example.se2030helpdesk.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface  EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByActiveTrueOrderByEventDateAscEventTimeAsc();
    List<Event> findByEventDateGreaterThanEqualOrderByEventDateAscEventTimeAsc(LocalDate date);
    List<Event> findByEventDateBetweenOrderByEventDateAscEventTimeAsc(LocalDate startDate, LocalDate endDate);
    List<Event> findByCreatedByIdAndActiveTrueOrderByEventDateAscEventTimeAsc(Long createdById);
    
    @Query("SELECT e FROM Event e WHERE e.eventDate = :date AND e.active = true ORDER BY e.eventTime ASC")
    List<Event> findEventsByDate(@Param("date") LocalDate date);
    
    @Query("SELECT e FROM Event e WHERE e.hallRoomNumber = :hall AND e.eventDate = :date AND e.eventTime = :time AND e.active = true")
    List<Event> findOverlappingEvents(@Param("hall") String hall, @Param("date") LocalDate date, @Param("time") java.time.LocalTime time);
}
