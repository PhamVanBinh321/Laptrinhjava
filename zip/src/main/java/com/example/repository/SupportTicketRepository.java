package com.example.repository;

import com.example.model.SupportTicket;
import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.model.SupportTicket;
import com.example.model.SupportTicket.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, Integer> {
    List<SupportTicket> findByUser(User user);
    List<SupportTicket> findByStatus(SupportTicket.Status status);
    List<SupportTicket> findByType(SupportTicket.TicketType type);

    // Tìm kiếm theo từ khóa (title, message, user name) và lọc theo status
    @Query("SELECT t FROM SupportTicket t JOIN t.user u " +
       "WHERE (:keyword IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
       "OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
       "AND (:status IS NULL OR t.status = :status)")
Page<SupportTicket> searchTickets(@Param("keyword") String keyword,
                                  @Param("status") Status status,
                                  Pageable pageable);
}
