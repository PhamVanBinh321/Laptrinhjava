package com.example.controller;

import com.example.model.*;
import com.example.repository.*;
import com.example.service.VietQrService; // <-- thêm
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    private final VietQrService vietQrService; // <-- thêm

    private static final int POINT_VALUE = 1; // 1 điểm = 100đ

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

    // FullCalendar fetch busy slots
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
                     @RequestParam(name = "paymentMethod", required = false) Booking.PaymentMethod paymentMethod,
                     @RequestParam(name = "payment", required = false) String paymentRaw,
                     @RequestParam(defaultValue = "false") boolean usePoints,
                     RedirectAttributes redirectAttributes) {

    if (auth == null) {
        redirectAttributes.addAttribute("error", "Vui lòng đăng nhập để đặt lịch");
        return "redirect:/login";
    }

    try {
        User customer = userRepository.findByUsername(auth.getUsername()).orElseThrow();
        User stylist  = userRepository.findById(stylistId).orElseThrow();

        // Tổng phút của dịch vụ (để check trùng)
        int totalMinutes = serviceRepository.findAllById(serviceIds).stream()
                .map(Service::getDuration).filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
        if (totalMinutes <= 0) totalMinutes = 60;
        LocalDateTime chosenEnd = datetime.plusMinutes(totalMinutes);

        // Check trùng
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
        if (conflict) {
            redirectAttributes.addAttribute("error", "Thời gian bạn chọn bị trùng. Vui lòng chọn thời gian khác.");
            return "redirect:/booking";
        }

        // Xác định phương thức thanh toán
        Booking.PaymentMethod pm = paymentMethod;
        if (pm == null && paymentRaw != null) {
            pm = "online".equalsIgnoreCase(paymentRaw)
                    ? Booking.PaymentMethod.ONLINE
                    : Booking.PaymentMethod.OFFLINE;
        }
        if (pm == null) pm = Booking.PaymentMethod.OFFLINE;

        // Lưu booking
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setStylist(stylist);
        booking.setBookingTime(datetime);
        booking.setPaymentMethod(pm);
        booking.setStatus(Booking.Status.PENDING);
        booking = bookingRepository.save(booking);

        // Lưu booking_service
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

        // Áp điểm
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

        redirectAttributes.addAttribute("success", "Đặt lịch thành công! Cảm ơn bạn đã đặt lịch.");

        if (pm == Booking.PaymentMethod.ONLINE) {
            return "redirect:/booking/payment/qr?id=" + booking.getId();
        }
        return "redirect:/booking/success?id=" + booking.getId();
        
    } catch (Exception e) {
        redirectAttributes.addAttribute("error", "Có lỗi xảy ra khi đặt lịch. Vui lòng thử lại.");
        return "redirect:/booking";
    }
}

    // Trang hiển thị QR (VietQR)
    @GetMapping("/booking/payment/qr")
public String showQr(@RequestParam("id") Integer bookingId, Model model) {
    Booking b = bookingRepository.findById(bookingId).orElseThrow();

    long amount = Math.round(b.getTotalAmount() != null ? b.getTotalAmount() : 0d);
    String info = "HH BOOKING #" + b.getId();

    String qrImg = vietQrService.buildImageUrl(amount, info);

    model.addAttribute("booking", b);
    model.addAttribute("amount", amount);
    model.addAttribute("addInfo", info);
    model.addAttribute("qrImg", qrImg);
    model.addAttribute("bank", vietQrService.getBankCode());
    model.addAttribute("account", vietQrService.getAccount());
    return "booking/payment-qr";
}

    // Trang thành công (cho cả OFFLINE & ONLINE sau khi KH bấm “Tôi đã chuyển – Quay lại”)
    @GetMapping("/booking/success")
    public String success(@RequestParam("id") Integer bookingId, Model model) {
        Booking b = bookingRepository.findById(bookingId).orElseThrow();
        model.addAttribute("booking", b);
        return "booking/success";
    }
}
