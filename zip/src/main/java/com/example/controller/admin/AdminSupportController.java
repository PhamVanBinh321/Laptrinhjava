// src/main/java/com/example/controller/AdminSupportController.java
package com.example.controller.admin;

import com.example.model.SupportTicket;
import com.example.service.SupportTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminSupportController {

    @Autowired
    private SupportTicketService supportService;

    // Hiển thị danh sách ticket
    @GetMapping("/admin/support")
    public String listSupportTickets(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Pageable pageable = PageRequest.of(page, 10);
        Page<SupportTicket> tickets = supportService.getTickets(keyword, status, pageable);

        model.addAttribute("tickets", tickets.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", tickets.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);

        return "admin/support-list"; // Trả về template Thymeleaf
    }

    // Đánh dấu đã xử lý
    @GetMapping("/admin/support/resolve")
    public String resolveTicket(@RequestParam("id") Integer id) {
        supportService.resolveTicket(id);
        return "redirect:/admin/support";
    }

    // Trả lời ticket (hiển thị form reply - bạn có thể mở rộng sau)
    @GetMapping("/admin/support/reply")
    public String showReplyForm(@RequestParam("id") Integer id, Model model) {
        SupportTicket ticket = supportService.findById(id);
        if (ticket == null) {
            return "redirect:/admin/support";
        }
        model.addAttribute("ticket", ticket);
        return "admin/support-reply"; // (tạo thêm nếu cần form trả lời)
    }
}