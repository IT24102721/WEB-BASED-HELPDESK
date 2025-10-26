package org.example.se2030helpdesk.dto;

public class TicketReplyDto {
    private Long ticketId;
    private Long userId;
    private String message;

    public TicketReplyDto() {}

    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
