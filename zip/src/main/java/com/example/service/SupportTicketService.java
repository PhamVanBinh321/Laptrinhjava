package com.example.service;

import com.example.model.SupportTicket;
import com.example.model.User;
import java.util.List;
import java.util.Optional;

public interface SupportTicketService {
    List<SupportTicket> getAllTickets();
    Optional<SupportTicket> getById(Integer id);
    List<SupportTicket> getByUser(User user);
    List<SupportTicket> getByStatus(SupportTicket.Status status);
    List<SupportTicket> getByType(SupportTicket.TicketType type);
    SupportTicket saveTicket(SupportTicket ticket);
    void deleteTicket(Integer id);
}
