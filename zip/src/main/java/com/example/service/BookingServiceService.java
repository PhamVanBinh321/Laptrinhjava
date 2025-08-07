package com.example.service;

import com.example.model.Booking;
import com.example.model.BookingService;
import java.util.List;

public interface BookingServiceService {
    List<BookingService> findByBooking(Booking booking);
}
