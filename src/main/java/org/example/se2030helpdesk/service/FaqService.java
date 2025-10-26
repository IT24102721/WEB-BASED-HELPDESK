package org.example.se2030helpdesk.service;

import org.example.se2030helpdesk.model.Faq;
import org.example.se2030helpdesk.repository.FaqRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class  FaqService {
    private final FaqRepository faqRepository;

    public FaqService(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    public List<Faq> findAll() { return faqRepository.findAll(); }
    public Optional<Faq> findById(Long id) { return faqRepository.findById(id); }
    public List<Faq> search(String keyword) { return faqRepository.findByQuestionContainingIgnoreCase(keyword); }
    public List<Faq> findByCategory(String category) { return faqRepository.findByCategory(category); }
    public Faq save(Faq faq) { return faqRepository.save(faq); }
    public void deleteById(Long id) { faqRepository.deleteById(id); }
}


