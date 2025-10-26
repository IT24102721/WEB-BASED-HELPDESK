package org.example.se2030helpdesk.controller;

import org.example.se2030helpdesk.model.Faq;
import org.example.se2030helpdesk.service.FaqService;
import org.example.se2030helpdesk.service.ValidationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/faqs")
@CrossOrigin
public class  FaqController {
    private final FaqService faqService;
    private final ValidationService validationService;
    
    public FaqController(FaqService faqService, ValidationService validationService){ 
        this.faqService = faqService; 
        this.validationService = validationService;
    }

    @GetMapping
    public List<Faq> list(@RequestParam(value = "q", required = false) String q,
                         @RequestParam(value = "category", required = false) String category){
        if(q!=null && !q.isBlank()) return faqService.search(q);
        if(category!=null && !category.isBlank()) return faqService.findByCategory(category);
        return faqService.findAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Faq faq){ 
        // Validate FAQ content
        ValidationService.ValidationResult contentValidation = validationService.validateFaqContent(
            faq.getQuestion(), 
            faq.getAnswer()
        );
        
        if (!contentValidation.isValid()) {
            return ResponseEntity.badRequest().body(Map.of("error", contentValidation.getErrorMessage()));
        }
        
        // Validate FAQ category
        ValidationService.ValidationResult categoryValidation = validationService.validateFaqCategory(faq.getCategory());
        
        if (!categoryValidation.isValid()) {
            return ResponseEntity.badRequest().body(Map.of("error", categoryValidation.getErrorMessage()));
        }
        
        // Trim fields
        if (faq.getQuestion() != null) {
            faq.setQuestion(faq.getQuestion().trim());
        }
        if (faq.getAnswer() != null) {
            faq.setAnswer(faq.getAnswer().trim());
        }
        if (faq.getCategory() != null) {
            faq.setCategory(faq.getCategory().trim());
        }
        
        try {
            Faq savedFaq = faqService.save(faq);
            return ResponseEntity.ok(savedFaq);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to save FAQ: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Faq incoming){
        return faqService.findById(id)
                .map(f -> {
                    // Prepare updated values
                    String newQuestion = incoming.getQuestion() != null ? incoming.getQuestion() : f.getQuestion();
                    String newAnswer = incoming.getAnswer() != null ? incoming.getAnswer() : f.getAnswer();
                    String newCategory = incoming.getCategory() != null ? incoming.getCategory() : f.getCategory();
                    
                    // Validate FAQ content if question or answer is being updated
                    ValidationService.ValidationResult contentValidation = validationService.validateFaqContent(
                        newQuestion, 
                        newAnswer
                    );
                    
                    if (!contentValidation.isValid()) {
                        return ResponseEntity.badRequest().body(Map.of("error", contentValidation.getErrorMessage()));
                    }
                    
                    // Validate FAQ category if it's being updated
                    ValidationService.ValidationResult categoryValidation = validationService.validateFaqCategory(newCategory);
                    
                    if (!categoryValidation.isValid()) {
                        return ResponseEntity.badRequest().body(Map.of("error", categoryValidation.getErrorMessage()));
                    }
                    
                    // Update fields
                    f.setQuestion(incoming.getQuestion() != null ? incoming.getQuestion().trim() : f.getQuestion());
                    f.setAnswer(incoming.getAnswer() != null ? incoming.getAnswer().trim() : f.getAnswer());
                    f.setCategory(incoming.getCategory() != null ? incoming.getCategory().trim() : f.getCategory());
                    
                    try {
                        Faq savedFaq = faqService.save(f);
                        return ResponseEntity.ok(savedFaq);
                    } catch (Exception e) {
                        return ResponseEntity.badRequest().body(Map.of("error", "Failed to update FAQ: " + e.getMessage()));
                    }
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){ faqService.deleteById(id); return ResponseEntity.noContent().build(); }
}


