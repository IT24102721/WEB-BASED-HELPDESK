package org.example.se2030helpdesk.dto;

public class TicketDto {
    private String title;
    private String description;
    private String category;
    private Long studentId;
    private String email;
    
    public TicketDto() {}
    
    public String getTitle()  { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
