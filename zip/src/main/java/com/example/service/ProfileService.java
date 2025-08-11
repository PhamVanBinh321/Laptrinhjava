// ProfileService.java
package com.example.service;

import com.example.model.*;
import com.example.repository.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final FeedbackRepository feedbackRepository;
    private final SupportTicketRepository supportTicketRepository;
    private final LoyaltyPointRepository loyaltyPointRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ProfileData loadProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + username));

        int points = calcPoints(user.getId());

        // Feedbacks của user -> map bookingId đã feedback
        List<Feedback> feedbacks = feedbackRepository.findByCustomer_IdOrderByCreatedAtDesc(user.getId());
        Set<Integer> bookingIdsWithFeedback = feedbacks.stream()
                .map(f -> f.getBooking() != null ? f.getBooking().getId() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Bookings của user -> map ra ViewModel
        List<Booking> bookings = bookingRepository.findByCustomer_IdOrderByBookingTimeDesc(user.getId());
        List<ProfileBookingVM> bookingVMs = bookings.stream().map(b -> {
            String stylistName = (b.getStylist() != null)
                    ? Optional.ofNullable(b.getStylist().getFullName()).filter(s -> !s.isBlank())
                      .orElse(b.getStylist().getUsername())
                    : "—";

            // Tên dịch vụ từ booking_service
            String serviceNames = (b.getBookingServices() == null) ? "—" :
                    b.getBookingServices().stream()
                            .map(bs -> {
                                if (bs.getService() != null && bs.getService().getName() != null) {
                                    return bs.getService().getName();
                                }
                                return "Dịch vụ";
                            })
                            .collect(Collectors.joining(", "));

            // totalAmount có thể là Double -> chuyển sang BigDecimal an toàn
            BigDecimal total;
            Object amountObj = b.getTotalAmount();
            if (amountObj instanceof BigDecimal) {
                total = (BigDecimal) amountObj;
            } else if (amountObj instanceof Double) {
                total = BigDecimal.valueOf((Double) amountObj);
            } else if (amountObj == null) {
                total = BigDecimal.ZERO;
            } else if (amountObj instanceof Integer) {
                total = BigDecimal.valueOf((Integer) amountObj);
            } else if (amountObj instanceof Long) {
                total = BigDecimal.valueOf((Long) amountObj);
            } else {
                // fallback nếu kiểu khác
                total = new BigDecimal(amountObj.toString());
            }

            String dateStr = b.getBookingTime() != null ? DATE_FMT.format(b.getBookingTime()) : "";
            String status = b.getStatus() != null ? b.getStatus().name() : "PENDING";
            boolean feedbackGiven = bookingIdsWithFeedback.contains(b.getId());

            return new ProfileBookingVM(
                    b.getId(),
                    dateStr,
                    stylistName,
                    serviceNames,
                    total,
                    status,
                    feedbackGiven
            );
        }).toList();

        // FeedbackVM
        List<ProfileFeedbackVM> feedbackVMs = feedbacks.stream().map(f -> {
            String dateStr = f.getCreatedAt() != null ? DATE_FMT.format(f.getCreatedAt()) : "";
            String serviceNames = (f.getBooking() != null && f.getBooking().getBookingServices() != null)
                    ? f.getBooking().getBookingServices().stream()
                        .map(bs -> bs.getService() != null ? bs.getService().getName() : "Dịch vụ")
                        .collect(Collectors.joining(", "))
                    : "—";
            return new ProfileFeedbackVM(
                    dateStr,
                    serviceNames,
                    f.getRating(),
                    safe(f.getComment()),
                    safe(f.getReply())
            );
        }).toList();

        // Support ticket VM
        List<ProfileSupportVM> supportVMs = supportTicketRepository
                .findByUser_IdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(s -> new ProfileSupportVM(
                        s.getCreatedAt() != null ? DATE_FMT.format(s.getCreatedAt()) : "",
                        safe(s.getTitle()),
                        s.getStatus() != null ? s.getStatus().name() : "OPEN",
                        safe(s.getReply())
                )).toList();

        return new ProfileData(user, points, bookingVMs, feedbackVMs, supportVMs);
    }

    private int calcPoints(Integer userId) {
        List<LoyaltyPoint> list = loyaltyPointRepository.findByCustomer_IdOrderByCreatedAtDesc(userId);
        int sum = 0;
        for (LoyaltyPoint lp : list) {
            if (lp.getType() != null && lp.getPoints() != null) {
                switch (lp.getType().name()) {
                    case "EARN" -> sum += lp.getPoints();
                    case "REDEEM" -> sum -= lp.getPoints();
                    default -> {}
                }
            }
        }
        return Math.max(sum, 0);
    }

    private String safe(String s) { return s == null ? "" : s; }

    // ===== View models =====
    public record ProfileBookingVM(
            Integer id,
            String dateStr,
            String stylistName,
            String serviceNames,
            BigDecimal total,
            String status,
            boolean feedbackGiven
    ) {}

    public record ProfileFeedbackVM(
            String dateStr,
            String serviceNames,
            Integer rating,
            String comment,
            String reply
    ) {}

    public record ProfileSupportVM(
            String dateStr,
            String title,
            String status,
            String answer
    ) {}

    public record ProfileData(
            User user,
            int points,
            List<ProfileBookingVM> bookings,
            List<ProfileFeedbackVM> feedbacks,
            List<ProfileSupportVM> supports
    ) {}
}
