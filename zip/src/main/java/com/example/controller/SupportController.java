// src/main/java/com/example/controller/SupportController.java
package com.example.controller;

import com.example.model.User;
import com.example.repository.SupportTicketRepository;
import com.example.repository.UserRepository;
import com.example.service.SupportService;
import com.example.dto.SupportTicketForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class SupportController {

    private final UserRepository userRepository;
    private final SupportTicketRepository supportTicketRepository;
    private final SupportService supportService;

    @GetMapping("/support")
    public String page(Authentication auth,
                       @RequestParam(value = "success", required = false) String success,
                       @RequestParam(value = "error", required = false) String error,
                       Model model) {
        model.addAttribute("form", new SupportTicketForm());
        model.addAttribute("success", success);
        model.addAttribute("error", error);

        if (auth != null) {
            userRepository.findByUsername(auth.getName()).ifPresent(u -> {
                model.addAttribute("currentUser", u);
                model.addAttribute("tickets",
                        supportTicketRepository.findByUser_IdOrderByCreatedAtDesc(u.getId()));
            });
        }
        return "support/index";
    }

    @PostMapping("/support")
    public String submit(Authentication auth,
                         @Valid @ModelAttribute("form") SupportTicketForm form,
                         BindingResult binding,
                         @RequestParam(value = "image", required = false) MultipartFile image,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (auth == null) {
            redirectAttributes.addAttribute("error", "Vui lòng đăng nhập để gửi yêu cầu hỗ trợ.");
            return "redirect:/login";
        }

        User user = userRepository.findByUsername(auth.getName()).orElse(null);
        if (user == null) {
            redirectAttributes.addAttribute("error", "Không tìm thấy thông tin người dùng.");
            return "redirect:/login";
        }

        if (binding.hasErrors()) {
            model.addAttribute("tickets",
                    supportTicketRepository.findByUser_IdOrderByCreatedAtDesc(user.getId()));
            model.addAttribute("currentUser", user);
            return "support/index";
        }

        try {
            supportService.create(user, form.getType(), form.getTitle(), form.getMessage(), image);
            redirectAttributes.addAttribute("success", "Yêu cầu hỗ trợ đã được gửi thành công!");
        } catch (IOException e) {
            redirectAttributes.addAttribute("error", "Upload ảnh lỗi: " + e.getMessage());
        }

        return "redirect:/support";
    }
}