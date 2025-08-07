package com.example.controller.admin;

import com.example.service.UserService;
import com.example.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class AdminUserController {

    @Autowired
    private UserService userService;

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
        if (!avatarFile.isEmpty()) user.setAvatar(avatarFile.getOriginalFilename());
        // TODO: Xử lý upload file thật
        userService.saveUser(user);
        redirectAttributes.addFlashAttribute("success", "Tạo tài khoản thành công!");
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
        if (!avatarFile.isEmpty()) user.setAvatar(avatarFile.getOriginalFilename());
        user.setId(id);
        userService.saveUser(user);
        redirectAttributes.addFlashAttribute("success", "Cập nhật tài khoản thành công!");
        return "redirect:/admin/users";
    }

    // Xóa user
    @GetMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("success", "Đã xóa tài khoản!");
        return "redirect:/admin/users";
    }

    // Khóa/Mở user (toggle status)
    @GetMapping("/admin/users/status/{id}")
    public String toggleUserStatus(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        User user = userService.getUserById(id).orElseThrow();
        user.setStatus(user.getStatus() == User.Status.ACTIVE ? User.Status.BLOCKED : User.Status.ACTIVE);
        userService.saveUser(user);
        redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái thành công!");
        return "redirect:/admin/users";
    }

    // Reset mật khẩu
    @GetMapping("/admin/users/resetpw/{id}")
    public String resetPassword(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        User user = userService.getUserById(id).orElseThrow();
        user.setPassword("123456"); // TODO: Mã hóa nếu có bảo mật
        userService.saveUser(user);
        redirectAttributes.addFlashAttribute("success", "Đã reset mật khẩu về 123456!");
        return "redirect:/admin/users";
    }
}
