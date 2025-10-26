package org.example.se2030helpdesk.service;

import org.example.se2030helpdesk.model.Feedback;
import org.example.se2030helpdesk.model.Ticket;
import org.example.se2030helpdesk.model.User;
import org.example.se2030helpdesk.repository.FeedbackRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public List<Feedback> findAll() { return feedbackRepository.findAll(); }
    public Optional<Feedback> findById(Long id) { return feedbackRepository.findById(id); }
    public List<Feedback> findByTicket(Ticket ticket) { return feedbackRepository.findByTicket(ticket); }
    public List<Feedback> findByStudent(User student) { return feedbackRepository.findByStudent(student); }
    public Feedback save(Feedback feedback) { return feedbackRepository.save(feedback); }
    public void deleteById(Long id) { feedbackRepository.deleteById(id); }
}


