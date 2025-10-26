package org.example.se2030helpdesk.service;

import org.example.se2030helpdesk.model.Ticket;
import org.example.se2030helpdesk.model.TicketReply;
import org.example.se2030helpdesk.model.User;
import org.example.se2030helpdesk.repository.TicketReplyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketReplyService {
    private final TicketReplyRepository ticketReplyRepository;

    public TicketReplyService(TicketReplyRepository ticketReplyRepository) {
        this.ticketReplyRepository = ticketReplyRepository;
    }

    public List<TicketReply> findByTicket(Ticket ticket) {
        return ticketReplyRepository.findByTicketOrderByCreatedAtAsc(ticket);
    }

    public TicketReply save(TicketReply ticketReply) {
        return ticketReplyRepository.save(ticketReply);
    }

    public Optional<TicketReply> findById(Long id) {
        return ticketReplyRepository.findById(id);
    }

    public void deleteById(Long id) {
        ticketReplyRepository.deleteById(id);
    }
}
