package com.example.controller.admin;

import com.example.service.SalaryDTO;
import com.example.service.SalaryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.YearMonth;
import java.util.List;

@Controller
@RequestMapping("/admin/salaries")
public class AdminSalaryController {

    private final SalaryService salaryService;

    public AdminSalaryController(SalaryService salaryService) {
        this.salaryService = salaryService;
    }

    @GetMapping
    public String list(
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
            Model model) {
        
        // Xử lý trường hợp month null
        YearMonth currentMonth = month != null ? month : YearMonth.now();
        
        List<SalaryDTO> salaries = salaryService.getSalariesOfMonth(currentMonth);
        
        model.addAttribute("month", currentMonth);
        model.addAttribute("salaries", salaries);
        return "admin/salary-list";
    }

    @PostMapping("/update")
    public String update(
            @RequestParam Integer stylistId,
            @RequestParam Double baseSalary,
            @RequestParam Double percent,
            @RequestParam String redirectMonth,
            RedirectAttributes redirectAttributes) {
        
        try {
            salaryService.updateBaseSalaryAndCommission(stylistId, baseSalary, percent);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cập nhật thất bại: " + e.getMessage());
        }
        
        return "redirect:/admin/salaries?month=" + redirectMonth;
    }
}