package com.example.repository;

import com.example.model.SupportTicket;
import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, Integer> {
    List<SupportTicket> findByUser(User user);
    List<SupportTicket> findByStatus(SupportTicket.Status status);
    List<SupportTicket> findByType(SupportTicket.TicketType type);
}
