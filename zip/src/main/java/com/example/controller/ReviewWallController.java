// src/main/java/com/example/controller/ReviewWallController.java
package com.example.controller;

import com.example.repository.UserRepository;
import com.example.service.ReviewWallService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewWallController {

    private final ReviewWallService wallService;
    private final UserRepository userRepository; // <-- thêm

    @GetMapping
    public String index(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @AuthenticationPrincipal UserDetails principal,
                        Model model) {

        var data = wallService.load(page, size);
        model.addAttribute("feedbackPage", data.page());
        model.addAttribute("avgRating", data.avgRating());
        model.addAttribute("totalReviews", data.totalReviews());
        model.addAttribute("page", page);
        model.addAttribute("size", size);

        // Đưa currentUser (có avatar) vào model nếu đã đăng nhập
        if (principal != null) {
            userRepository.findByUsername(principal.getUsername())
                    .ifPresent(u -> model.addAttribute("currentUser", u));
        }
        return "reviews/index";
    }

    @PostMapping
    public String post(@RequestParam Integer rating,
                       @RequestParam String comment,
                       @AuthenticationPrincipal UserDetails principal,
                       RedirectAttributes ra) {
        if (principal == null) {
            ra.addFlashAttribute("error", "Vui lòng đăng nhập để gửi đánh giá.");
            return "redirect:/login";
        }
        try {
            wallService.post(principal.getUsername(), rating, comment);
            ra.addFlashAttribute("ok", "Cảm ơn bạn! Đánh giá đã được gửi.");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/reviews";
    }
}
