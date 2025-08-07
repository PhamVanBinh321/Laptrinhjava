package com.example.service;


import com.example.model.Booking;
import com.example.model.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingService {
    List<Booking> getAllBookings();
    Optional<Booking> getBookingById(Integer id);
    List<Booking> getBookingsByCustomer(User customer);
    List<Booking> getBookingsByStylist(User stylist);
    List<Booking> getBookingsByStatus(Booking.Status status);
    List<Booking> getBookingsByTimeRange(LocalDateTime from, LocalDateTime to);
    Booking saveBooking(Booking booking);
    void deleteBooking(Integer id);
    List<Booking> findAllForAdmin(String keyword, String status);
void updateStatus(Integer id, Booking.Status status);

}
