package com.example.controller.auth;

import com.example.dto.RegisterRequest;
import com.example.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // templates/login.html
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("form", new RegisterRequest());
        return "register"; // templates/register.html
    }

    @PostMapping("/register")
    public String doRegister(@ModelAttribute("form") @Valid RegisterRequest form,
                             BindingResult binding, Model model) {
        if (binding.hasErrors()) return "register";
        try {
            userService.registerCustomer(form);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "register";
        }
        return "redirect:/login?registered";
    }
}
