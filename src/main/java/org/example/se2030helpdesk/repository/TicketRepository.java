package org.example.se2030helpdesk.repository;

import org.example.se2030helpdesk.model.Ticket;
import org.example.se2030helpdesk.model.TicketStatus;
import org.example.se2030helpdesk.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByStudent(User student);
    List<Ticket> findByAssignedTo(User assignedTo);
    List<Ticket> findByStatus(TicketStatus status);
}


