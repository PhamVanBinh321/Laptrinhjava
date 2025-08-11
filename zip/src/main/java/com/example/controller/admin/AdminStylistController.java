package com.example.controller.admin;

import com.example.model.StylistProfile;
import com.example.model.User;
import com.example.service.StylistProfileService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Quản trị Stylist – dùng đúng các hàm Service cũ.
 */
@Controller
@RequestMapping("/admin/stylists")
public class AdminStylistController {

    @Autowired
    private StylistProfileService stylistProfileService;

    @Autowired
    private UserService userService;
    
    // Đường dẫn upload từ application.properties
    @Value("${upload.avatar.path}")
    private String uploadPath;

    /* ========== 1. DANH SÁCH ========== */
    @GetMapping
    public String listStylists(@RequestParam(required = false) String keyword,
                               @RequestParam(required = false) String level,
                               Model model) {

        List<StylistProfile> stylists = stylistProfileService.searchStylists(keyword, level);

        model.addAttribute("stylists", stylists);
        model.addAttribute("keyword", keyword);
        model.addAttribute("level", level);
        return "admin/stylist-list";
    }

    /* ========== 2. FORM THÊM ========== */
    @GetMapping("/new")
    public String addForm(Model model) {
        StylistProfile profile = new StylistProfile();
        User user = new User();
        // Set default required values
        user.setStatus(com.example.model.User.Status.ACTIVE);
        user.setPassword("123456");
        user.setRole(com.example.model.User.Role.STYLIST); // Set role mặc định
        profile.setUser(user);
        model.addAttribute("stylist", profile);
        model.addAttribute("isEdit", false);
        return "admin/stylist-form";
    }

    /* ========== 3. FORM SỬA ========== */
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id,
                           Model model,
                           RedirectAttributes ra) {

        Optional<StylistProfile> opt = stylistProfileService.getByUserId(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("error", "Không tìm thấy stylist!");
            return "redirect:/admin/stylists";
        }
        model.addAttribute("stylist", opt.get());
        model.addAttribute("isEdit", true);
        return "admin/stylist-form";
    }

    /* ========== 4. LƯU (THÊM / SỬA) ========== */
    @PostMapping("/save")
    public String saveStylist(@ModelAttribute("stylist") StylistProfile stylist,
                              @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
                              RedirectAttributes ra) {
        String avatarFileName = null;
        String oldAvatar = null;
        
        try {
            // Xử lý upload avatar nếu có
            if (avatarFile != null && !avatarFile.isEmpty()) {
                avatarFileName = handleFileUpload(avatarFile);
            }

            /* 4.1  Nếu là stylist mới (chưa có userId)   */
            if (stylist.getUserId() == null) {
                // Set avatar nếu có upload
                if (avatarFileName != null) {
                    stylist.getUser().setAvatar(avatarFileName);
                }
                
                // Set default values nếu chưa có
                if (stylist.getUser().getPassword() == null || stylist.getUser().getPassword().isEmpty()) {
                    stylist.getUser().setPassword("123456");
                }
                if (stylist.getUser().getRole() == null) {
                    stylist.getUser().setRole(com.example.model.User.Role.STYLIST);
                }
                if (stylist.getUser().getStatus() == null) {
                    stylist.getUser().setStatus(com.example.model.User.Status.ACTIVE);
                }
                
                // Lưu User trước để lấy id
                User savedUser = userService.saveUser(stylist.getUser());
                stylist.setUserId(savedUser.getId());
                stylist.setUser(savedUser);
            } else {
                /* 4.2  Nếu là cập nhật */
                Optional<User> existingUserOpt = userService.getUserById(stylist.getUserId());
                if (existingUserOpt.isPresent()) {
                    User existingUser = existingUserOpt.get();
                    User formUser = stylist.getUser();
                    oldAvatar = existingUser.getAvatar();

                    // Cập nhật các trường user
                    existingUser.setFullName(formUser.getFullName());
                    existingUser.setEmail(formUser.getEmail());
                    existingUser.setPhone(formUser.getPhone());
                    existingUser.setUsername(formUser.getUsername());
                    existingUser.setStatus(formUser.getStatus());
                    existingUser.setRole(formUser.getRole());

                    // Chỉ cập nhật password nếu có nhập mới
                    if (formUser.getPassword() != null && !formUser.getPassword().isEmpty()) {
                        existingUser.setPassword(formUser.getPassword());
                    }

                    // Xử lý avatar - chỉ set avatar mới nếu có upload
                    if (avatarFileName != null) {
                        existingUser.setAvatar(avatarFileName);
                    }
                    // Nếu không có upload avatar mới, giữ nguyên avatar cũ

                    User savedUser = userService.saveUser(existingUser);
                    stylist.setUser(savedUser);
                    stylist.setUserId(savedUser.getId()); // Đảm bảo set lại userId
                } else {
                    // Nếu user không tồn tại, tạo mới như stylist mới
                    if (avatarFileName != null) {
                        stylist.getUser().setAvatar(avatarFileName);
                    }
                    // Set default values nếu chưa có
                    if (stylist.getUser().getPassword() == null || stylist.getUser().getPassword().isEmpty()) {
                        stylist.getUser().setPassword("123456");
                    }
                    if (stylist.getUser().getRole() == null) {
                        stylist.getUser().setRole(com.example.model.User.Role.STYLIST);
                    }
                    if (stylist.getUser().getStatus() == null) {
                        stylist.getUser().setStatus(com.example.model.User.Status.ACTIVE);
                    }
                    User savedUser = userService.saveUser(stylist.getUser());
                    stylist.setUserId(savedUser.getId());
                    stylist.setUser(savedUser);
                }
            }

            /* 4.3  Lưu StylistProfile */
            stylistProfileService.saveStylist(stylist);

            ra.addFlashAttribute("success", "Đã lưu stylist thành công!");
            return "redirect:/admin/stylists";
            
        } catch (DataIntegrityViolationException e) {
            String message = e.getMessage().toLowerCase();
            if (message.contains("email")) {
                ra.addFlashAttribute("error", "Email đã tồn tại trong hệ thống!");
            } else if (message.contains("username")) {
                ra.addFlashAttribute("error", "Tên đăng nhập đã tồn tại trong hệ thống!");
            } else {
                ra.addFlashAttribute("error", "Dữ liệu đã tồn tại trong hệ thống!");
            }
            // Xóa file đã upload nếu có lỗi
            if (avatarFileName != null) {
                deleteAvatarFile(avatarFileName);
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi khi lưu stylist: " + e.getMessage());
            // Xóa file đã upload nếu có lỗi
            if (avatarFileName != null) {
                deleteAvatarFile(avatarFileName);
            }
        }
        return "redirect:/admin/stylists";
    }

    /* ========== 5. KHÓA / MỞ KHÓA ========== */
    @GetMapping("/block/{id}")
    public String block(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            userService.updateStatus(id, com.example.model.User.Status.BLOCKED);
            ra.addFlashAttribute("success", "Đã khóa stylist!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi khi khóa stylist: " + e.getMessage());
        }
        return "redirect:/admin/stylists";
    }

    @GetMapping("/unblock/{id}")
    public String unblock(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            userService.updateStatus(id, com.example.model.User.Status.ACTIVE);
            ra.addFlashAttribute("success", "Đã mở khóa stylist!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi khi mở khóa stylist: " + e.getMessage());
        }
        return "redirect:/admin/stylists";
    }
    
    // Phương thức xử lý upload file
    private String handleFileUpload(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Kiểm tra loại file
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IOException("Chỉ cho phép upload file ảnh!");
        }

        // Tạo thư mục upload nếu chưa tồn tại
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // Tạo tên file duy nhất
        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();
        }
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        // Lưu file
        Path filePath = uploadDir.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFileName;
    }

    // Phương thức xóa file avatar
    private void deleteAvatarFile(String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            try {
                Path filePath = Paths.get(uploadPath).resolve(fileName);
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                System.err.println("Không thể xóa file avatar: " + fileName + " - " + e.getMessage());
            }
        }
    }
}