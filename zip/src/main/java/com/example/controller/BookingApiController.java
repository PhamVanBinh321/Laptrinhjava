package com.example.controller;

import com.example.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/booking/api")
public class BookingApiController {

    private final BookingRepository bookingRepository;

    @GetMapping("/booking/api/stylists/{stylistId}/busy")
public List<Map<String, Object>> busy(
        @PathVariable Integer stylistId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

    return bookingRepository
            .findByStylist_IdAndBookingTimeBetween(stylistId, start, end)
            .stream()
            .map(b -> {
                int mins = (b.getBookingServices() == null) ? 0 :
                        b.getBookingServices().stream()
                                .map(bs -> bs.getDuration() == null ? 0 : bs.getDuration())
                                .reduce(0, Integer::sum);

            // Dùng Map.<String,Object>of(...) để giá trị là Object
                return Map.<String, Object>of(
                        "title", "Đã đặt",
                        "start", b.getBookingTime().toString(),                    // ISO string
                        "end",   b.getBookingTime().plusMinutes(mins).toString()  // ISO string
                );
            })
            .toList();
}

}
