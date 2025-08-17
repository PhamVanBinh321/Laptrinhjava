package com.example.controller;

import com.example.model.StylistProfile;
import com.example.service.StylistProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class StylistController {

    private final StylistProfileService stylistService;

    @GetMapping("/stylists")
    public String list(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "sort", defaultValue = "name") String sort,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "9") int size,
            Model model
    ) {
        Page<StylistProfile> stylists = stylistService.findPage(q, sort, page, size);
        model.addAttribute("stylists", stylists);
        model.addAttribute("q", q);
        model.addAttribute("sort", sort);
        model.addAttribute("currentPath", "/stylists");
        return "stylists"; // templates/stylists.html
    }

    @GetMapping("/stylists/{id}")
    public String detail(@PathVariable("id") Integer userId, Model model) {
        StylistProfile sp = stylistService.getById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Stylist not found with id: " + userId));
        model.addAttribute("stylist", sp);
        model.addAttribute("currentPath", "/stylists");
        return "stylist-detail"; // templates/stylist-detail.html
    }
}
