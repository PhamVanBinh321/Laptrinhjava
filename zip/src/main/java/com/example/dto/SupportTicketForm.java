// src/main/java/com/example/web/dto/SupportTicketForm.java
package com.example.dto;

import com.example.model.SupportTicket;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SupportTicketForm {

    private SupportTicket.TicketType type; // booking/payment/...

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank
    @Size(max = 1000)
    private String message;

    // getters/setters
    public SupportTicket.TicketType getType() { return type; }
    public void setType(SupportTicket.TicketType type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
