package org.example.se2030helpdesk.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "issue_reports")
public class  IssueReport {
    public enum Status { OPEN, IN_PROGRESS, RESOLVED }

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "resource_id")
    private Resource resource;

    @Column(name = "issue_description", nullable = false, length = 2000)
    private String issueDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.OPEN;

    @Column(name = "admin_comments", length = 1000)
    private String adminComments;

    @Column(name = "report_date", nullable = false, updatable = false)
    private Instant reportDate = Instant.now();

    @Column(name = "resolved_date")
    private Instant resolvedDate;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Resource getResource() { return resource; }
    public void setResource(Resource resource) { this.resource = resource; }

    public String getIssueDescription() { return issueDescription; }
    public void setIssueDescription(String issueDescription) { this.issueDescription = issueDescription; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getAdminComments() { return adminComments; }
    public void setAdminComments(String adminComments) { this.adminComments = adminComments; }

    public Instant getReportDate() { return reportDate; }
    public void setReportDate(Instant reportDate) { this.reportDate = reportDate; }

    public Instant getResolvedDate() { return resolvedDate; }
    public void setResolvedDate(Instant resolvedDate) { this.resolvedDate = resolvedDate; }
}
