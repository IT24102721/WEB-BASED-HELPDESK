package org.example.se2030helpdesk.controller;

import org.example.se2030helpdesk.model.Feedback;
import org.example.se2030helpdesk.service.FeedbackService;
import org.example.se2030helpdesk.service.ValidationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin
public class  FeedbackController {
    private final FeedbackService feedbackService;
    private final ValidationService validationService;
    
    public FeedbackController(FeedbackService feedbackService, ValidationService validationService){ 
        this.feedbackService = feedbackService; 
        this.validationService = validationService;
    }

    @GetMapping
    public List<Feedback> list(){ return feedbackService.findAll(); }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Feedback feedback){ 
        // Validate feedback
        ValidationService.ValidationResult validationResult = validationService.validateFeedback(
            feedback.getRating(), 
            feedback.getDescription()
        );
        
        if (!validationResult.isValid()) {
            return ResponseEntity.badRequest().body(Map.of("error", validationResult.getErrorMessage()));
        }
        
        // Trim description if provided
        if (feedback.getDescription() != null) {
            feedback.setDescription(feedback.getDescription().trim());
        }
        
        try {
            Feedback savedFeedback = feedbackService.save(feedback);
            return ResponseEntity.ok(savedFeedback);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to save feedback: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Feedback incoming){
        return feedbackService.findById(id)
                .map(f -> {
                    // Validate feedback if rating or description is being updated
                    String newRating = incoming.getRating() != null ? incoming.getRating() : f.getRating();
                    String newDescription = incoming.getDescription() != null ? incoming.getDescription() : f.getDescription();
                    
                    ValidationService.ValidationResult validationResult = validationService.validateFeedback(
                        newRating, 
                        newDescription
                    );
                    
                    if (!validationResult.isValid()) {
                        return ResponseEntity.badRequest().body(Map.of("error", validationResult.getErrorMessage()));
                    }
                    
                    // Update fields
                    if(incoming.getName()!=null) f.setName(incoming.getName());
                    if(incoming.getEmail()!=null) f.setEmail(incoming.getEmail());
                    if(incoming.getRating()!=null) f.setRating(incoming.getRating());
                    if(incoming.getDescription()!=null) f.setDescription(incoming.getDescription().trim());
                    
                    try {
                        Feedback savedFeedback = feedbackService.save(f);
                        return ResponseEntity.ok(savedFeedback);
                    } catch (Exception e) {
                        return ResponseEntity.badRequest().body(Map.of("error", "Failed to update feedback: " + e.getMessage()));
                    }
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){ feedbackService.deleteById(id); return ResponseEntity.noContent().build(); }
}


