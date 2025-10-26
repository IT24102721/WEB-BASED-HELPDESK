package org.example.se2030helpdesk.repository;

import org.example.se2030helpdesk.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByPerformedByOrderByTimestampDesc(String performedBy);
    List<AuditLog> findByActionOrderByTimestampDesc(String action);
    List<AuditLog> findAllByOrderByTimestampDesc();
}
