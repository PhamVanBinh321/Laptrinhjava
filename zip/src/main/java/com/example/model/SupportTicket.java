package com.example.model;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "support_tickets")
public class SupportTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Quan hệ với User (người tạo ticket)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TicketType type;

    @Column(length = 100)
    private String title;

    @Lob
    private String message;

    @Column(length = 255)
    private String image;

    @Enumerated(EnumType.STRING)
    @Column(length = 15)
    private Status status = Status.OPEN;

    @Lob
    private String reply;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Enum cho type
    public enum TicketType {
        booking, payment, account, technical, suggestion, other
    }

    // Enum cho status
    public enum Status {
        OPEN, IN_PROGRESS, RESOLVED, CLOSED
    }

    // Constructors
    public SupportTicket() {}

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TicketType getType() {
        return type;
    }

    public void setType(TicketType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
