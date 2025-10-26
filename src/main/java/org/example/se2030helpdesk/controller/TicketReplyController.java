package org.example.se2030helpdesk.controller;

import org.example.se2030helpdesk.dto.TicketReplyDto;
import org.example.se2030helpdesk.model.Ticket;
import org.example.se2030helpdesk.model.TicketReply;
import org.example.se2030helpdesk.model.User;
import org.example.se2030helpdesk.repository.UserRepository;
import org.example.se2030helpdesk.service.TicketReplyService;
import org.example.se2030helpdesk.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/ticket-replies")
@CrossOrigin
public class  TicketReplyController {
    private final TicketReplyService ticketReplyService;
    private final TicketService ticketService;
    private final UserRepository userRepository;

    public TicketReplyController(TicketReplyService ticketReplyService, TicketService ticketService, UserRepository userRepository) {
        this.ticketReplyService = ticketReplyService;
        this.ticketService = ticketService;
        this.userRepository = userRepository;
    }

    @GetMapping("/ticket/{ticketId}")
    public List<TicketReply> getRepliesByTicket(@PathVariable Long ticketId) {
        Optional<Ticket> ticket = ticketService.findById(ticketId);
        if (ticket.isEmpty()) {
            return List.of();
        }
        return ticketReplyService.findByTicket(ticket.get());
    }

    @PostMapping
    public ResponseEntity<?> createReply(@RequestBody TicketReplyDto replyDto) {
        try {
            if (replyDto.getTicketId() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Ticket ID must be specified"));
            }

            if (replyDto.getUserId() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "User ID must be specified"));
            }

            if (replyDto.getMessage() == null || replyDto.getMessage().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Message must be specified"));
            }

            Optional<Ticket> ticket = ticketService.findById(replyDto.getTicketId());
            if (ticket.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Ticket not found"));
            }

            Optional<User> user = userRepository.findById(replyDto.getUserId());
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }

            TicketReply reply = new TicketReply();
            reply.setTicket(ticket.get());
            reply.setUser(user.get());
            reply.setMessage(replyDto.getMessage().trim());

            TicketReply savedReply = ticketReplyService.save(reply);
            return ResponseEntity.ok(savedReply);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReply(@PathVariable Long id) {
        try {
            ticketReplyService.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Reply deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to delete reply: " + e.getMessage()));
        }
    }
}
