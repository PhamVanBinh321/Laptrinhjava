package com.example.repository;


import com.example.model.Booking;
import com.example.model.BookingService;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingServiceRepository extends JpaRepository<BookingService, BookingService.BookingServiceId> {
    // Có thể custom filter theo bookingId, serviceId
    List<BookingService> findByBooking(Booking booking);
}
