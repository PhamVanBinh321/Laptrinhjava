package com.example.controller.admin;

import com.example.service.UserService;
import com.example.model.User;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Controller
public class AdminUserController {

    @Autowired
    private UserService userService;

    // Đường dẫn upload từ application.properties
    @Value("${upload.path}")
    private String uploadPath;

    // Danh sách tài khoản, tab lọc vai trò
    @GetMapping("/admin/users")
    public String userList(@RequestParam(required = false) String role, Model model,
                           @ModelAttribute("success") String success, @ModelAttribute("error") String error) {
        List<User> users;
        if (role == null || role.equals("ALL")) {
            users = userService.getAllUsers();
            model.addAttribute("roleActive", "ALL");
        } else {
            users = userService.getUsersByRole(User.Role.valueOf(role));
            model.addAttribute("roleActive", role);
        }
        model.addAttribute("users", users);
        if (!success.isEmpty()) model.addAttribute("success", success);
        if (!error.isEmpty()) model.addAttribute("error", error);
        return "admin/user-list";
    }

    // Thêm mới
    @GetMapping("/admin/users/new")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", Arrays.asList(User.Role.values()));
        model.addAttribute("actionUrl", "/admin/users/new");
        return "admin/user-form";
    }

    @PostMapping("/admin/users/new")
    public String addUser(@ModelAttribute("user") User user,
                          @RequestParam("avatarFile") MultipartFile avatarFile,
                          RedirectAttributes redirectAttributes) {
        try {
            // Xử lý upload file
            String avatarFileName = handleFileUpload(avatarFile);
            if (avatarFileName != null) {
                user.setAvatar(avatarFileName);
            }
            
            userService.saveUser(user);
            redirectAttributes.addFlashAttribute("success", "Tạo tài khoản thành công!");
        } catch (DataIntegrityViolationException e) {
            // Xử lý lỗi trùng lặp email/username
            String message = e.getMessage();
            if (message.contains("users.email")) {
                redirectAttributes.addFlashAttribute("error", "Email đã tồn tại trong hệ thống!");
            } else if (message.contains("users.username")) {
                redirectAttributes.addFlashAttribute("error", "Tên đăng nhập đã tồn tại!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Dữ liệu đã tồn tại!");
            }
            // Xóa file đã upload nếu có lỗi
            if (user.getAvatar() != null) {
                deleteAvatarFile(user.getAvatar());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi tạo tài khoản: " + e.getMessage());
            // Xóa file đã upload nếu có lỗi
            if (user.getAvatar() != null) {
                deleteAvatarFile(user.getAvatar());
            }
        }
        return "redirect:/admin/users";
    }

    // Sửa user
    @GetMapping("/admin/users/edit/{id}")
    public String showEditUserForm(@PathVariable Integer id, Model model) {
        User user = userService.getUserById(id).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("roles", Arrays.asList(User.Role.values()));
        model.addAttribute("actionUrl", "/admin/users/edit/" + id);
        return "admin/user-form";
    }

    @PostMapping("/admin/users/edit/{id}")
    public String editUser(@PathVariable Integer id,
                           @ModelAttribute("user") User user,
                           @RequestParam("avatarFile") MultipartFile avatarFile,
                           RedirectAttributes redirectAttributes) {
        try {
            User existingUser = userService.getUserById(id).orElseThrow();
            
            // Lưu lại avatar cũ để xử lý nếu cần
            String oldAvatar = existingUser.getAvatar();
            
            // Nếu có upload ảnh mới
            String avatarFileName = handleFileUpload(avatarFile);
            if (avatarFileName != null) {
                user.setAvatar(avatarFileName);
            } else {
                // Giữ nguyên ảnh cũ
                user.setAvatar(existingUser.getAvatar());
            }
            
            user.setId(id);
            userService.saveUser(user);
            redirectAttributes.addFlashAttribute("success", "Cập nhật tài khoản thành công!");
            
            // Xóa avatar cũ nếu có upload ảnh mới
            if (avatarFileName != null && oldAvatar != null && !oldAvatar.equals(avatarFileName)) {
                deleteAvatarFile(oldAvatar);
            }
            
        } catch (DataIntegrityViolationException e) {
            // Xử lý lỗi trùng lặp email/username khi cập nhật
            String message = e.getMessage();
            if (message.contains("users.email")) {
                redirectAttributes.addFlashAttribute("error", "Email đã tồn tại trong hệ thống!");
            } else if (message.contains("users.username")) {
                redirectAttributes.addFlashAttribute("error", "Tên đăng nhập đã tồn tại!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Dữ liệu đã tồn tại!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật tài khoản: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // Xóa user
    @GetMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserById(id).orElseThrow();
            String avatarFileName = user.getAvatar();
            
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "Đã xóa tài khoản!");
            
            // Xóa file avatar nếu tồn tại
            if (avatarFileName != null) {
                deleteAvatarFile(avatarFileName);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa tài khoản: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // Khóa/Mở user (toggle status)
    @GetMapping("/admin/users/status/{id}")
    public String toggleUserStatus(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserById(id).orElseThrow();
            user.setStatus(user.getStatus() == User.Status.ACTIVE ? User.Status.BLOCKED : User.Status.ACTIVE);
            userService.saveUser(user);
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật trạng thái: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // Reset mật khẩu
    @GetMapping("/admin/users/resetpw/{id}")
    public String resetPassword(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserById(id).orElseThrow();
            user.setPassword("123456"); // TODO: Mã hóa nếu có bảo mật
            userService.saveUser(user);
            redirectAttributes.addFlashAttribute("success", "Đã reset mật khẩu về 123456!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi reset mật khẩu: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // Phương thức xử lý upload file
    private String handleFileUpload(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
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
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
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
                // Log lỗi nhưng không throw exception để không ảnh hưởng đến flow chính
                System.err.println("Không thể xóa file avatar: " + fileName + " - " + e.getMessage());
            }
        }
    }
}