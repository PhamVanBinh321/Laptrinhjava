package com.example.service.impl;

import com.example.model.SupportTicket;
import com.example.model.User;
import com.example.repository.SupportTicketRepository;
import com.example.service.SupportTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import com.example.model.SupportTicket;
import com.example.model.SupportTicket.Status;
import com.example.repository.SupportTicketRepository;
import com.example.service.SupportTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
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
     @Override
    public Page<SupportTicket> getTickets(String keyword, String status, Pageable pageable) {
        Status statusEnum = null;
        if (status != null && !status.isEmpty()) {
            try {
                statusEnum = Status.valueOf(status);
            } catch (IllegalArgumentException e) {
                // Nếu giá trị status không hợp lệ, bỏ qua lọc theo trạng thái
            }
        }
        return supportTicketRepository.searchTickets(keyword, statusEnum, pageable);
    }

    @Override
    public SupportTicket findById(Integer id) {
        return supportTicketRepository.findById(id).orElse(null);
    }

    @Override
    public void save(SupportTicket ticket) {
        supportTicketRepository.save(ticket);
    }

    @Override
    public void resolveTicket(Integer id) {
        SupportTicket ticket = findById(id);
        if (ticket != null && ticket.getStatus() != Status.CLOSED) {
            ticket.setStatus(Status.RESOLVED);
            save(ticket);
        }
    }
}
