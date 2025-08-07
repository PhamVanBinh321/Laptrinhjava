// src/main/java/com/example/controller/admin/AdminFinanceReportController.java
package com.example.controller.admin;

import com.example.dto.FinanceReportDTO;
import com.example.service.FinanceReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.YearMonth;
import java.util.List;

@Controller
public class AdminFinanceReportController {

    private final FinanceReportService reportService;

    public AdminFinanceReportController(FinanceReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/admin/finance-report")
public String viewReport(
        @RequestParam(required = false)
        @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
        Model model) {

    if (month == null) month = YearMonth.now();

    FinanceReportDTO dto = reportService.buildReport(month);
    if (dto == null) {
        dto = new FinanceReportDTO(0, 0, 0, 0, List.of(), List.of());
    }
    model.addAttribute("month", month);
    model.addAttribute("totals", dto);
    model.addAttribute("serviceStats", dto.getServiceStats());
    model.addAttribute("topCustomers", dto.getTopCustomers());

    return "admin/finance-report";
}


}
