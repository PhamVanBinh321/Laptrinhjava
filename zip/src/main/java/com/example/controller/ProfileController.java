// ProfileController.java
package com.example.controller;

import com.example.service.ProfileService;
import com.example.service.ProfileService.ProfileData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/profile")
    public String profile(Authentication auth, Model model) {
        // Bắt buộc đăng nhập (nên đã cấu hình trong Security để /profile requires auth)
        String username = auth.getName();

        ProfileData data = profileService.loadProfile(username);

        model.addAttribute("user", data.user());
        model.addAttribute("points", data.points());
        model.addAttribute("bookings", data.bookings());
        model.addAttribute("feedbacks", data.feedbacks());
        model.addAttribute("supports", data.supports());

        // để header highlight đúng menu
        model.addAttribute("currentPath", "/profile");

        return "profile";
    }
}
