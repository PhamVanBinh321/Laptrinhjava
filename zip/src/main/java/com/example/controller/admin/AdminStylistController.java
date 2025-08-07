package com.example.controller.admin;

import com.example.model.StylistProfile;
import com.example.model.User;
import com.example.service.StylistProfileService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

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

    /* ========== 1. DANH SÁCH ========== */
    @GetMapping
    public String listStylists(@RequestParam(required = false) String keyword,
                               @RequestParam(required = false) String level,
                               Model model) {

        List<StylistProfile> stylists = stylistProfileService.searchStylists(keyword, level);

        model.addAttribute("stylists", stylists);
        model.addAttribute("keyword", keyword);
        model.addAttribute("level", level);
        return "admin/stylist-list";                  // resources/templates/admin/stylist-list.html
    }

    /* ========== 2. FORM THÊM ========== */
    @GetMapping("/new")
    public String addForm(Model model) {
        StylistProfile profile = new StylistProfile();
        profile.setUser(new User());                  // để bind các trường User trong form
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
                              RedirectAttributes ra) {

        /* 4.1  Nếu là stylist mới (chưa có userId)   */
        if (stylist.getUserId() == null) {
            // Lưu User trước để lấy id (giả sử UserService có saveUser)
            User savedUser = userService.saveUser(stylist.getUser());
            stylist.setUserId(savedUser.getId());
            stylist.setUser(savedUser);
        } else {
            /* 4.2  Nếu là cập nhật – giữ nguyên avatar / password cũ (nếu form không nhập) */
            userService.getUserById(stylist.getUserId()).ifPresent(existingUser -> {
                User formUser = stylist.getUser();

                // chỉ cập nhật những trường được nhập
                existingUser.setFullName(formUser.getFullName());
                existingUser.setEmail(formUser.getEmail());
                existingUser.setPhone(formUser.getPhone());
                existingUser.setUsername(formUser.getUsername());
                existingUser.setStatus(formUser.getStatus());

                // avatar & password: giữ nguyên nếu form không có file / không đổi mật khẩu
                userService.saveUser(existingUser);
                stylist.setUser(existingUser);
            });
        }

        /* 4.3  Lưu StylistProfile */
        stylistProfileService.saveStylist(stylist);

        ra.addFlashAttribute("success", "Đã lưu stylist thành công!");
        return "redirect:/admin/stylists";
    }

    /* ========== 5. KHÓA / MỞ KHÓA ========== */
    @GetMapping("/block/{id}")
    public String block(@PathVariable Integer id, RedirectAttributes ra) {
        userService.updateStatus(id, User.Status.BLOCKED);
        ra.addFlashAttribute("success", "Đã khóa stylist!");
        return "redirect:/admin/stylists";
    }

    @GetMapping("/unblock/{id}")
    public String unblock(@PathVariable Integer id, RedirectAttributes ra) {
        userService.updateStatus(id, User.Status.ACTIVE);
        ra.addFlashAttribute("success", "Đã mở khóa stylist!");
        return "redirect:/admin/stylists";
    }
}
