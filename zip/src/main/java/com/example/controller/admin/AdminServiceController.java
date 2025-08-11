package com.example.controller.admin;

import com.example.model.Service;
import com.example.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/services")
public class AdminServiceController {

    @Autowired
    private ServiceService serviceService;

    // Đường dẫn upload từ application.properties
    @Value("${upload.service.path}")
    private String uploadPath;

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
        try {
            // Nếu là sửa, giữ nguyên ảnh nếu không upload ảnh mới
            if (id != null) {
                Service old = serviceService.getServiceById(id).orElse(null);
                if (old != null && (imageFile == null || imageFile.isEmpty())) {
                    service.setImage(old.getImage());
                }
            }
            
            // Xử lý upload ảnh
            String imageFileName = handleFileUpload(imageFile);
            if (imageFileName != null) {
                service.setImage(imageFileName);
            }
            
            serviceService.saveService(service);
            ra.addFlashAttribute("success", "Đã lưu dịch vụ thành công!");
            
        } catch (DataIntegrityViolationException e) {
            String message = e.getMessage();
            if (message.contains("services.name")) {
                ra.addFlashAttribute("error", "Tên dịch vụ đã tồn tại!");
            } else {
                ra.addFlashAttribute("error", "Dữ liệu đã tồn tại!");
            }
            // Xóa file đã upload nếu có lỗi
            if (service.getImage() != null) {
                deleteImageFile(service.getImage());
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi khi lưu dịch vụ: " + e.getMessage());
            // Xóa file đã upload nếu có lỗi
            if (service.getImage() != null) {
                deleteImageFile(service.getImage());
            }
        }
        return "redirect:/admin/services";
    }

    // Xóa dịch vụ
    @GetMapping("/delete/{id}")
    public String deleteService(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            Service service = serviceService.getServiceById(id).orElse(null);
            if (service == null) {
                ra.addFlashAttribute("error", "Không tìm thấy dịch vụ!");
                return "redirect:/admin/services";
            }
            
            String imageFileName = service.getImage();
            serviceService.deleteService(id);
            ra.addFlashAttribute("success", "Đã xóa dịch vụ!");
            
            // Xóa file ảnh nếu tồn tại
            if (imageFileName != null) {
                deleteImageFile(imageFileName);
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Không thể xóa dịch vụ: " + e.getMessage());
        }
        return "redirect:/admin/services";
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
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        // Lưu file
        Path filePath = uploadDir.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFileName;
    }

    // Phương thức xóa file ảnh
    private void deleteImageFile(String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            try {
                Path filePath = Paths.get(uploadPath).resolve(fileName);
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                // Log lỗi nhưng không throw exception để không ảnh hưởng đến flow chính
                System.err.println("Không thể xóa file ảnh: " + fileName + " - " + e.getMessage());
            }
        }
    }
}