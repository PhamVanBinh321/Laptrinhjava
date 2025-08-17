// src/main/java/com/example/service/SupportService.java
package com.example.service;

import com.example.model.SupportTicket;
import com.example.model.User;
import com.example.repository.SupportTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupportService {

    private final SupportTicketRepository supportTicketRepository;

    private static final Path ROOT = Paths.get("uploads/support");

    public SupportTicket create(User user,
                                SupportTicket.TicketType type,
                                String title,
                                String message,
                                MultipartFile imageFile) throws IOException {
        String imagePublicPath = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            imagePublicPath = store(imageFile); // ví dụ: /uploads/support/xxx.png
        }

        SupportTicket t = new SupportTicket();
        t.setUser(user);
        t.setType(type);
        t.setTitle(title);
        t.setMessage(message);
        t.setImage(imagePublicPath);
        t.setStatus(SupportTicket.Status.OPEN);
        t.setCreatedAt(LocalDateTime.now());

        return supportTicketRepository.save(t);
    }

    private String store(MultipartFile file) throws IOException {
        if (!Files.exists(ROOT)) Files.createDirectories(ROOT);

        // Giới hạn 5MB
        if (file.getSize() > 5L * 1024 * 1024) {
            throw new IOException("Ảnh vượt quá 5MB");
        }

        // Lấy đuôi file
        String original = Objects.requireNonNullElse(file.getOriginalFilename(), "");
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0 && dot < original.length() - 1) ext = original.substring(dot);

        String name = UUID.randomUUID().toString().replace("-", "") + ext;
        Path target = ROOT.resolve(name).normalize();
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        // Trả đường dẫn public (khớp ResourceHandler bên dưới)
        return "/uploads/support/" + name;
    }
}
