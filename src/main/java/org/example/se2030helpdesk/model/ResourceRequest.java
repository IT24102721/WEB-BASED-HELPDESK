package org.example.se2030helpdesk.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "resource_requests")
public class ResourceRequest {
    public enum Status { PENDING, APPROVED, REJECTED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "resource_id")
    private Resource resource;

    @Column(nullable = false, length = 500)
    private String purpose;

    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.PENDING;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User admin;

    @Column(name = "admin_comments", length = 1000)
    private String adminComments;

    @Column(name = "request_date", nullable = false, updatable = false)
    private Instant requestDate = Instant.now();

    @Column(name = "approved_date")
    private Instant approvedDate;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Resource getResource() { return resource; }
    public void setResource(Resource resource) { this.resource = resource; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public Integer getDurationDays() { return durationDays; }
    public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public User getAdmin() { return admin; }
    public void setAdmin(User admin) { this.admin = admin; }

    public String getAdminComments() { return adminComments; }
    public void setAdminComments(String adminComments) { this.adminComments = adminComments; }

    public Instant getRequestDate() { return requestDate; }
    public void setRequestDate(Instant requestDate) { this.requestDate = requestDate; }

    public Instant getApprovedDate() { return approvedDate; }
    public void setApprovedDate(Instant approvedDate) { this.approvedDate = approvedDate; }
}
