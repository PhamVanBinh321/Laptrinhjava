package com.example.controller;

import com.example.model.*;
import com.example.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class BookingController {

    private final StylistProfileRepository stylistProfileRepository;
    private final ServiceRepository serviceRepository;
    private final BookingRepository bookingRepository;
    private final BookingServiceRepository bookingServiceRepository;
    private final LoyaltyPointRepository loyaltyPointRepository;
    private final UserRepository userRepository;

    private static final int POINT_VALUE = 100; // 1 điểm = 100đ (tuỳ bạn đổi)

    @GetMapping("/booking")
    public String page(Model model,
                       @AuthenticationPrincipal org.springframework.security.core.userdetails.User auth) {

        model.addAttribute("stylists", stylistProfileRepository.findAll());
        model.addAttribute("services", serviceRepository.findByStatus(Service.Status.ACTIVE));
        model.addAttribute("pointValue", POINT_VALUE);

        int points = 0;
        if (auth != null) {
            userRepository.findByUsername(auth.getUsername()).ifPresent(u -> {
                int sum = loyaltyPointRepository.findByCustomer_IdOrderByCreatedAtDesc(u.getId())
                        .stream()
                        .mapToInt(lp -> lp.getType() == LoyaltyPoint.Type.EARN ? lp.getPoints() : -lp.getPoints())
                        .sum();
                model.addAttribute("points", Math.max(sum, 0));
            });
        } else {
            model.addAttribute("points", points);
        }
        return "booking/index";
    }

    // Lịch bận của stylist (FullCalendar gọi khi prev/next)
    @ResponseBody
    @GetMapping("/booking/api/stylists/{stylistId}/busy")
    public List<Map<String, Object>> busy(
            @PathVariable Integer stylistId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        List<Booking> list = bookingRepository.findByStylist_IdAndBookingTimeBetween(stylistId, start, end);

        return list.stream().map(b -> {
            int mins = (b.getBookingServices() == null) ? 0 :
                    b.getBookingServices().stream()
                            .map(BookingService::getDuration)
                            .filter(Objects::nonNull)
                            .mapToInt(Integer::intValue).sum();
            if (mins <= 0) mins = 60;
            LocalDateTime endTime = b.getBookingTime().plusMinutes(mins);

            Map<String, Object> ev = new HashMap<>();
            ev.put("start", b.getBookingTime().toString());
            ev.put("end", endTime.toString());
            ev.put("title", "Đã đặt");
            return ev;
        }).collect(Collectors.toList());
    }

    @PostMapping("/booking")
    @Transactional
    public String submit(@AuthenticationPrincipal org.springframework.security.core.userdetails.User auth,
                         @RequestParam Integer stylistId,
                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime datetime,
                         @RequestParam(name = "serviceIds") List<Integer> serviceIds,
                         @RequestParam(defaultValue = "OFFLINE") Booking.PaymentMethod paymentMethod,
                         @RequestParam(defaultValue = "false") boolean usePoints) {

        if (auth == null) return "redirect:/login";

        User customer = userRepository.findByUsername(auth.getUsername()).orElseThrow();
        User stylist  = userRepository.findById(stylistId).orElseThrow();

        // Tổng phút của dịch vụ chọn (để check trùng)
        int totalMinutes = serviceRepository.findAllById(serviceIds).stream()
                .map(Service::getDuration).filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
        if (totalMinutes <= 0) totalMinutes = 60;
        LocalDateTime chosenEnd = datetime.plusMinutes(totalMinutes);

        // Check trùng lịch đơn giản
        List<Booking> busy = bookingRepository.findByStylist_IdAndBookingTimeBetween(
                stylistId, datetime.minusHours(12), datetime.plusHours(12));
        boolean conflict = busy.stream().anyMatch(b -> {
            int mins = (b.getBookingServices()==null)?0:
                    b.getBookingServices().stream().map(BookingService::getDuration)
                            .filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
            if (mins<=0) mins=60;
            LocalDateTime bEnd = b.getBookingTime().plusMinutes(mins);
            return b.getBookingTime().isBefore(chosenEnd) && datetime.isBefore(bEnd);
        });
        if (conflict) return "redirect:/booking?conflict";

        // 1) Lưu booking trước để có ID
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setStylist(stylist);
        booking.setBookingTime(datetime);
        booking.setPaymentMethod(paymentMethod);
        booking.setStatus(Booking.Status.PENDING);
        booking = bookingRepository.save(booking);

        // 2) Lưu từng booking_service (PHẢI set bookingId & serviceId)
        double total = 0d;
        for (Integer sid : serviceIds) {
            Service svc = serviceRepository.findById(sid).orElseThrow();
            BookingService bs = new BookingService();
            bs.setBookingId(booking.getId());
            bs.setServiceId(svc.getId());
            bs.setBooking(booking);
            bs.setService(svc);
            bs.setPrice(svc.getPrice());
            bs.setDuration(svc.getDuration());
            bookingServiceRepository.save(bs);
            total += Optional.ofNullable(svc.getPrice()).orElse(0.0);
        }

        // 3) Tổng tiền & điểm
        if (usePoints) {
            int have = loyaltyPointRepository.findByCustomer_IdOrderByCreatedAtDesc(customer.getId())
                    .stream()
                    .mapToInt(lp -> lp.getType()==LoyaltyPoint.Type.EARN ? lp.getPoints() : -lp.getPoints())
                    .sum();
            int can = Math.max(have, 0);
            int redeemMoney = Math.min(can * POINT_VALUE, (int)Math.round(total * 0.1)); // trần 10%
            if (redeemMoney > 0) {
                int pointsToRedeem = redeemMoney / POINT_VALUE;
                LoyaltyPoint lp = new LoyaltyPoint();
                lp.setCustomer(customer);
                lp.setType(LoyaltyPoint.Type.REDEEM);
                lp.setPoints(pointsToRedeem);
                lp.setDescription("Dùng điểm khi đặt lịch #" + booking.getId());
                loyaltyPointRepository.save(lp);
                total -= redeemMoney;
            }
        }

        booking.setTotalAmount(total);
        bookingRepository.save(booking);

        return "redirect:/booking?success";
    }
}
