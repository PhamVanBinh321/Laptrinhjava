package com.example.controller;

import com.example.dto.ProfileEditDto;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.service.ProfileService;
import com.example.service.ProfileService.ProfileData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final UserRepository userRepository;

    // KHỚP WebConfig: addResourceLocations("file:" + avatarUploadPath)
    // application.properties: upload.avatar.path=uploads/avatar/
    @Value("${upload.avatar.path}")
    private String avatarUploadPath;

    @GetMapping("/profile")
    public String profile(Authentication auth, Model model,
                          @RequestParam(value = "success", required = false) String successMsg) {
        String username = auth.getName();
        ProfileData data = profileService.loadProfile(username);

        model.addAttribute("user", data.user());
        model.addAttribute("points", data.points());
        model.addAttribute("bookings", data.bookings());
        model.addAttribute("feedbacks", data.feedbacks());
        model.addAttribute("supports", data.supports());
        model.addAttribute("currentPath", "/profile");

        // Hiển thị thông báo qua query param
        if (successMsg != null) model.addAttribute("success", successMsg);

        return "profile";
    }

    @GetMapping("/profile/edit")
    public String edit(Authentication auth, Model model) {
        User u = userRepository.findByUsername(auth.getName()).orElseThrow();

        ProfileEditDto dto = new ProfileEditDto();
        dto.setFullName(u.getFullName());
        dto.setEmail(u.getEmail());
        dto.setPhone(u.getPhone());

        model.addAttribute("dto", dto);
        model.addAttribute("currentAvatar", u.getAvatar()); // VD: /uploads/avatar/abc.jpg
        model.addAttribute("currentPath", "/profile");
        return "profile-edit";
    }

    @PostMapping("/profile/edit")
    @Transactional
    public String update(Authentication auth,
                         @Valid @ModelAttribute("dto") ProfileEditDto dto,
                         BindingResult br,
                         @RequestParam(name="avatarFile", required=false) MultipartFile avatarFile,
                         Model model,
                         RedirectAttributes redirectAttributes) throws IOException {

        User u = userRepository.findByUsername(auth.getName()).orElseThrow();

        // Check trùng email của user khác
        if (userRepository.existsByEmailAndIdNot(dto.getEmail().trim(), u.getId())) {
            br.rejectValue("email", "email.exists", "Email đã được sử dụng bởi tài khoản khác");
        }

        if (br.hasErrors()) {
            model.addAttribute("currentAvatar", u.getAvatar());
            model.addAttribute("currentPath", "/profile");
            return "profile-edit";
        }

        u.setFullName(dto.getFullName().trim());
        u.setEmail(dto.getEmail().trim());
        u.setPhone(Optional.ofNullable(dto.getPhone()).map(String::trim).orElse(null));

        // Lưu avatar nếu có
        if (avatarFile != null && !avatarFile.isEmpty()) {
            String fileName = storeAvatar(avatarFile);               // ghi vào uploads/avatar/
            u.setAvatar("/uploads/avatar/" + fileName);              // URL public (KHỚP handler)
        }

        userRepository.save(u);

        // Dùng query param để báo thành công
        redirectAttributes.addAttribute("success", "Cập nhật hồ sơ thành công!");
        return "redirect:/profile";
    }

    private String storeAvatar(MultipartFile file) throws IOException {
        // Đường dẫn vật lý tương đối, ví dụ: uploads/avatar/
        Path dir = Paths.get(avatarUploadPath).toAbsolutePath().normalize();
        Files.createDirectories(dir);

        String ori = file.getOriginalFilename();
        String ext = ".jpg";
        if (ori != null && ori.contains(".")) {
            ext = ori.substring(ori.lastIndexOf('.')).toLowerCase();
        }
        String safeName = "avt_" + System.currentTimeMillis() + "_" + UUID.randomUUID() + ext;
        Path target = dir.resolve(safeName);
        file.transferTo(target.toFile());
        return safeName;
    }
}
