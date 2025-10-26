package org.example.se2030helpdesk.repository;

import org.example.se2030helpdesk.model.Ticket;
import org.example.se2030helpdesk.model.TicketReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface  TicketReplyRepository extends JpaRepository<TicketReply, Long> {
    List<TicketReply> findByTicketOrderByCreatedAtAsc(Ticket ticket);
}
