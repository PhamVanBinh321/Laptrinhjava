package com.example.repository;


import com.example.model.Booking;
import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByCustomer(User customer);
    List<Booking> findByStylist(User stylist);
    List<Booking> findByStatus(Booking.Status status);
    List<Booking> findByBookingTimeBetween(LocalDateTime start, LocalDateTime end);
    // Thêm hàm filter theo ngày, trạng thái, stylist, customer...
}
