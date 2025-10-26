package org.example.se2030helpdesk.repository;

import org.example.se2030helpdesk.model.Faq;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface  FaqRepository extends JpaRepository<Faq, Long> {
    List<Faq> findByQuestionContainingIgnoreCase(String keyword);
    List<Faq> findByCategory(String category);
}


