package org.example.se2030helpdesk.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "event_requests")
public class  EventRequest {
    public enum Status { PENDING, APPROVED, REJECTED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false)
    private LocalDate proposedDate;

    @Column(nullable = false)
    private LocalTime proposedTime;

    @Column(nullable = false, length = 100)
    private String hallRoomNumber;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false, length = 100)
    private String requesterName;

    @Column(nullable = false, length = 150)
    private String requesterEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.PENDING;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    private Instant updatedAt;

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public LocalDate getProposedDate() { return proposedDate; }
    public void setProposedDate(LocalDate proposedDate) { this.proposedDate = proposedDate; }
    public LocalTime getProposedTime() { return proposedTime; }
    public void setProposedTime(LocalTime proposedTime) { this.proposedTime = proposedTime; }
    public String getHallRoomNumber() { return hallRoomNumber; }
    public void setHallRoomNumber(String hallRoomNumber) { this.hallRoomNumber = hallRoomNumber; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRequesterName() { return requesterName; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }
    public String getRequesterEmail() { return requesterEmail; }
    public void setRequesterEmail(String requesterEmail) { this.requesterEmail = requesterEmail; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}



