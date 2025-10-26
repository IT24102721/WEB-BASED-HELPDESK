package org.example.se2030helpdesk.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String performedBy;

    @Column
    private String targetUser;

    @Column
    private String details;

    @Column(nullable = false, updatable = false)
    private Instant timestamp = Instant.now();

    @Column
    private String ipAddress;

    public Long getId() { return id; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }
    public String getTargetUser() { return targetUser; }
    public void setTargetUser(String targetUser) { this.targetUser = targetUser; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public Instant getTimestamp() { return timestamp; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
}
