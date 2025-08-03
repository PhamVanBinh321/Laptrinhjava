package com.example.service.impl;

import com.example.model.SupportTicket;
import com.example.model.User;
import com.example.repository.SupportTicketRepository;
import com.example.service.SupportTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class SupportTicketServiceImpl implements SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;

    @Autowired
    public SupportTicketServiceImpl(SupportTicketRepository supportTicketRepository) {
        this.supportTicketRepository = supportTicketRepository;
    }

    @Override
    public List<SupportTicket> getAllTickets() {
        return supportTicketRepository.findAll();
    }

    @Override
    public Optional<SupportTicket> getById(Integer id) {
        return supportTicketRepository.findById(id);
    }

    @Override
    public List<SupportTicket> getByUser(User user) {
        return supportTicketRepository.findByUser(user);
    }

    @Override
    public List<SupportTicket> getByStatus(SupportTicket.Status status) {
        return supportTicketRepository.findByStatus(status);
    }

    @Override
    public List<SupportTicket> getByType(SupportTicket.TicketType type) {
        return supportTicketRepository.findByType(type);
    }

    @Override
    public SupportTicket saveTicket(SupportTicket ticket) {
        return supportTicketRepository.save(ticket);
    }

    @Override
    public void deleteTicket(Integer id) {
        supportTicketRepository.deleteById(id);
    }
}
