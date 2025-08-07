package com.example.service;

import com.example.model.SupportTicket;
import com.example.model.User;
import java.util.List;
import java.util.Optional;
import com.example.model.SupportTicket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface SupportTicketService {
    List<SupportTicket> getAllTickets();
    Optional<SupportTicket> getById(Integer id);
    List<SupportTicket> getByUser(User user);
    List<SupportTicket> getByStatus(SupportTicket.Status status);
    List<SupportTicket> getByType(SupportTicket.TicketType type);
    SupportTicket saveTicket(SupportTicket ticket);
    void deleteTicket(Integer id);

    /**
     * Tìm kiếm và phân trang ticket theo từ khóa và trạng thái
     */
    Page<SupportTicket> getTickets(String keyword, String status, Pageable pageable);

    /**
     * Lấy ticket theo ID
     */
    SupportTicket findById(Integer id);

    /**
     * Lưu hoặc cập nhật ticket
     */
    void save(SupportTicket ticket);

    /**
     * Đánh dấu ticket là đã xử lý (RESOLVED)
     */
    void resolveTicket(Integer id);
}
