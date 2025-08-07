package com.example.controller.admin;

import com.example.model.Booking;
import com.example.model.Booking.Status;
import com.example.service.BookingService;
import com.example.service.BookingServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Arrays;
import java.util.List;

@Controller
public class AdminBookingController {
    @Autowired private BookingService bookingService;
    @Autowired private BookingServiceService bookingServiceService;

    @GetMapping("/admin/bookings")
    public String listBookings(@RequestParam(required = false) String keyword,
                               @RequestParam(required = false) String status,
                               Model model,
                               @ModelAttribute("success") String success,
                               @ModelAttribute("error") String error) {
        List<Booking> bookings = bookingService.findAllForAdmin(keyword, status);
        // Gán danh sách dịch vụ cho mỗi booking (nếu cần)
        bookings.forEach(b -> b.setBookingServices(bookingServiceService.findByBooking(b)));
        model.addAttribute("bookings", bookings);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("statuses", Arrays.asList(Status.values()));
        if (!success.isEmpty()) model.addAttribute("success", success);
        if (!error.isEmpty()) model.addAttribute("error", error);
        return "admin/booking-list";
    }
@GetMapping("/admin/bookings/confirm/{id}")
public String confirmBooking(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
    bookingService.updateStatus(id, Booking.Status.CONFIRMED);
    redirectAttributes.addFlashAttribute("success", "Đã xác nhận lịch!");
    return "redirect:/admin/bookings";
}

@GetMapping("/admin/bookings/cancel/{id}")
public String cancelBooking(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
    bookingService.updateStatus(id, Booking.Status.CANCELLED);
    redirectAttributes.addFlashAttribute("success", "Đã hủy lịch!");
    return "redirect:/admin/bookings";
}

@GetMapping("/admin/bookings/done/{id}")
public String doneBooking(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
    bookingService.updateStatus(id, Booking.Status.DONE);
    redirectAttributes.addFlashAttribute("success", "Đã hoàn thành lịch!");
    return "redirect:/admin/bookings";
}

@GetMapping("/admin/bookings/noshow/{id}")
public String noShowBooking(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
    bookingService.updateStatus(id, Booking.Status.NO_SHOW);
    redirectAttributes.addFlashAttribute("success", "Đã chuyển trạng thái No-Show!");
    return "redirect:/admin/bookings";
}


    @GetMapping("/admin/bookings/detail/{id}")
    public String detailBooking(@PathVariable Integer id, Model model) {
        Booking booking = bookingService.getBookingById(id).orElseThrow();
        booking.setBookingServices(bookingServiceService.findByBooking(booking));
        model.addAttribute("booking", booking);
        return "admin/booking-detail";
    }
}
