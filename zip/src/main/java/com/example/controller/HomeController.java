package com.example.controller;

import com.example.model.Service;
import com.example.model.StylistProfile;
import com.example.repository.ServiceRepository;
import com.example.repository.StylistProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ServiceRepository serviceRepository;
    private final StylistProfileRepository stylistProfileRepository;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        // Top 6 dịch vụ ACTIVE (nếu có status)
        var servicesTop = serviceRepository.findByStatus(
                Service.Status.ACTIVE, PageRequest.of(0, 6)
        );

        // Top 6 stylist (tùy bạn lọc điều kiện khác, ví dụ level != null ...)
        var stylistsTop = stylistProfileRepository.findAll(PageRequest.of(0, 6)).getContent();

        model.addAttribute("servicesTop", servicesTop.getContent() != null ? servicesTop.getContent() : servicesTop);
        model.addAttribute("stylistsTop", stylistsTop);
        return "home/index";
    }
}
