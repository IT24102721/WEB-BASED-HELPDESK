package org.example.se2030helpdesk.repository;

import org.example.se2030helpdesk.model.Feedback;
import org.example.se2030helpdesk.model.Ticket;
import org.example.se2030helpdesk.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByTicket(Ticket ticket);
    List<Feedback> findByStudent(User student);
}


