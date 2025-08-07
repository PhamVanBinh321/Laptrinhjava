
package com.example.controller.admin;

import com.example.model.User;
import com.example.model.StylistProfile;
import com.example.service.BookingService;
import com.example.service.UserService;
import com.example.service.StylistProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AdminDashboardController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private StylistProfileService stylistProfileService;

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        // 1. Tổng lượt đặt lịch (booking)
        model.addAttribute("totalBookings", bookingService.getAllBookings().size());

        // 2. Tổng doanh thu tháng (ví dụ: sum totalAmount của các booking trong tháng hiện tại)
        double monthlyRevenue = bookingService.getAllBookings().stream()
            .filter(b -> b.getBookingTime().getMonthValue() == java.time.LocalDate.now().getMonthValue()
                      && b.getBookingTime().getYear() == java.time.LocalDate.now().getYear())
            .mapToDouble(b -> b.getTotalAmount() != null ? b.getTotalAmount() : 0)
            .sum();
        model.addAttribute("monthlyRevenue", String.format("%,.0f₫", monthlyRevenue));

        // 3. Số khách hàng mới trong tháng này (role=Customer, created_at trong tháng)
        long newCustomers = userService.getUsersByRole(User.Role.CUSTOMER).stream()
            .filter(u -> u.getCreatedAt() != null
                    && u.getCreatedAt().getMonthValue() == java.time.LocalDate.now().getMonthValue()
                    && u.getCreatedAt().getYear() == java.time.LocalDate.now().getYear())
            .count();
        model.addAttribute("newCustomers", newCustomers);

        // 4. Số stylist đang ACTIVE
        long activeStylists = stylistProfileService.getAllStylistProfiles().stream()
            .filter(profile -> profile.getUser() != null
                && profile.getUser().getStatus() == User.Status.ACTIVE)
            .count();
        model.addAttribute("activeStylists", activeStylists);

        // 5. Top stylist nổi bật (theo số khách đã phục vụ, ví dụ 3 người nhiều nhất)
        List<StylistProfile> topProfiles = stylistProfileService.getAllStylistProfiles().stream()
            .sorted((a, b) -> b.getServedCustomers() - a.getServedCustomers())
            .limit(3)
            .collect(Collectors.toList());
        model.addAttribute("topStylists", topProfiles);

        // 6. Chart doanh thu (labels là các ngày, data là tổng doanh thu mỗi ngày trong tháng này)
        List<String> revenueLabels = java.util.stream.IntStream.rangeClosed(1, java.time.YearMonth.now().lengthOfMonth())
            .mapToObj(i -> String.format("%02d", i))
            .collect(Collectors.toList());
        List<Double> revenueData = revenueLabels.stream()
            .map(day -> bookingService.getAllBookings().stream()
                .filter(b -> b.getBookingTime().getMonthValue() == java.time.LocalDate.now().getMonthValue()
                          && b.getBookingTime().getDayOfMonth() == Integer.parseInt(day)
                          && b.getBookingTime().getYear() == java.time.LocalDate.now().getYear())
                .mapToDouble(b -> b.getTotalAmount() != null ? b.getTotalAmount() : 0)
                .sum())
            .collect(Collectors.toList());
        model.addAttribute("revenueLabels", revenueLabels);
        model.addAttribute("revenueData", revenueData);

        return "admin/dashboard";
    }
}
