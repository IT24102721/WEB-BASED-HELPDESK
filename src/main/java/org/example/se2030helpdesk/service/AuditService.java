package org.example.se2030helpdesk.service;

import org.example.se2030helpdesk.model.AuditLog;
import org.example.se2030helpdesk.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditService {
    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logAction(String action, String performedBy, String targetUser, String details, String ipAddress) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setPerformedBy(performedBy);
        log.setTargetUser(targetUser);
        log.setDetails(details);
        log.setIpAddress(ipAddress);
        auditLogRepository.save(log);
    }

    public List<AuditLog> getLogsByUser(String username) {
        return auditLogRepository.findByPerformedByOrderByTimestampDesc(username);
    }

    public List<AuditLog> getLogsByAction(String action) {
        return auditLogRepository.findByActionOrderByTimestampDesc(action);
    }

    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc();
    }
}
