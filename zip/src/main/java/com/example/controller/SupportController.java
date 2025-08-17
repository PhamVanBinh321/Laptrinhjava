// src/main/java/com/example/web/SupportController.java
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

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class SupportController {

    private final UserRepository userRepository;
    private final SupportTicketRepository supportTicketRepository;
    private final SupportService supportService;

    @GetMapping("/support")
    public String page(Authentication auth,
                       @RequestParam(value = "ok", required = false) String ok,
                       Model model) {
        model.addAttribute("form", new SupportTicketForm());
        model.addAttribute("ok", ok != null);

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
                         Model model) {
        if (auth == null) return "redirect:/login";

        User user = userRepository.findByUsername(auth.getName()).orElse(null);
        if (user == null) return "redirect:/login";

        if (binding.hasErrors()) {
            model.addAttribute("tickets",
                    supportTicketRepository.findByUser_IdOrderByCreatedAtDesc(user.getId()));
            return "support/index";
        }

        try {
            supportService.create(user, form.getType(), form.getTitle(), form.getMessage(), image);
        } catch (IOException e) {
            binding.reject("upload", "Upload ảnh lỗi: " + e.getMessage());
            model.addAttribute("tickets",
                    supportTicketRepository.findByUser_IdOrderByCreatedAtDesc(user.getId()));
            return "support/index";
        }

        return "redirect:/support?ok=1";
    }
}
