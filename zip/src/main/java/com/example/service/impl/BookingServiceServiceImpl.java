package com.example.service.impl;

import com.example.model.Booking;
import com.example.model.BookingService;
import com.example.repository.BookingServiceRepository;
import com.example.service.BookingServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BookingServiceServiceImpl implements BookingServiceService {

    @Autowired
    private BookingServiceRepository bookingServiceRepository;

    @Override
    public List<BookingService> findByBooking(Booking booking) {
        return bookingServiceRepository.findByBooking(booking);
    }
}
