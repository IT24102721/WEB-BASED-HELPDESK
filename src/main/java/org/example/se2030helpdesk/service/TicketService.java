package org.example.se2030helpdesk.service;

import org.example.se2030helpdesk.model.Ticket;
import org.example.se2030helpdesk.model.TicketStatus;
import org.example.se2030helpdesk.model.User;
import org.example.se2030helpdesk.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class  TicketService {
    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public List<Ticket> findAll() { return ticketRepository.findAll(); }
    public Optional<Ticket> findById(Long id) { return ticketRepository.findById(id); }
    public List<Ticket> findByStudent(User student) { return ticketRepository.findByStudent(student); }
    public List<Ticket> findByAssigned(User staff) { return ticketRepository.findByAssignedTo(staff); }
    public List<Ticket> findByStatus(TicketStatus status) { return ticketRepository.findByStatus(status); }
    public Ticket save(Ticket ticket) { 
        return ticketRepository.save(ticket); 
    }
    public void deleteById(Long id) { ticketRepository.deleteById(id); }
}


