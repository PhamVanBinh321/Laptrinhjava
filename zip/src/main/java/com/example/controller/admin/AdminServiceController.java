package com.example.controller.admin;

import com.example.model.Service;
import com.example.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/services")
public class AdminServiceController {

    @Autowired
    private ServiceService serviceService;

    // Hiển thị danh sách dịch vụ với filter
    @GetMapping
    public String listServices(@RequestParam(required = false) String keyword,
                               @RequestParam(required = false) String status,
                               Model model) {
        List<Service> services = serviceService.searchServices(keyword, status);
        model.addAttribute("services", services);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        return "admin/service-list"; // Tên file Thymeleaf
    }

    // Trang thêm mới
    @GetMapping("/new")
    public String addServiceForm(Model model) {
        model.addAttribute("service", new Service());
        model.addAttribute("isEdit", false);
        return "admin/service-form"; // Tên file Thymeleaf (form thêm/sửa dùng chung)
    }

    // Trang sửa
    @GetMapping("/edit/{id}")
    public String editServiceForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Service service = serviceService.getServiceById(id).orElse(null);
        if (service == null) {
            ra.addFlashAttribute("error", "Không tìm thấy dịch vụ!");
            return "redirect:/admin/services";
        }
        model.addAttribute("service", service);
        model.addAttribute("isEdit", true);
        return "admin/service-form";
    }

    // Xử lý thêm/sửa dịch vụ
    @PostMapping({"/new", "/edit/{id}"})
    public String saveService(@ModelAttribute("service") Service service,
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                              @PathVariable(required = false) Integer id,
                              RedirectAttributes ra) {
        // Nếu là sửa, giữ nguyên ảnh nếu không upload ảnh mới
        if (id != null) {
            Service old = serviceService.getServiceById(id).orElse(null);
            if (old != null && (imageFile == null || imageFile.isEmpty())) {
                service.setImage(old.getImage());
            }
        }
        // Xử lý upload ảnh
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + StringUtils.cleanPath(imageFile.getOriginalFilename());
            String uploadDir = "uploads/services/";
            try {
                File uploadPath = new File(uploadDir);
                if (!uploadPath.exists()) uploadPath.mkdirs();
                imageFile.transferTo(new File(uploadDir + fileName));
                service.setImage(fileName);
            } catch (IOException e) {
                ra.addFlashAttribute("error", "Lỗi upload ảnh: " + e.getMessage());
            }
        }
        serviceService.saveService(service);
        ra.addFlashAttribute("success", "Đã lưu dịch vụ thành công!");
        return "redirect:/admin/services";
    }

    // Xóa dịch vụ
    @GetMapping("/delete/{id}")
    public String deleteService(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            serviceService.deleteService(id);
            ra.addFlashAttribute("success", "Đã xóa dịch vụ!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Không thể xóa dịch vụ: " + e.getMessage());
        }
        return "redirect:/admin/services";
    }
}
