package com.example.service.impl;


import com.example.model.Booking;
import com.example.model.User;
import com.example.repository.BookingRepository;
import com.example.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    
    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

   

    @Override
    public List<Booking> getBookingsByCustomer(User customer) {
        return bookingRepository.findByCustomer(customer);
    }

    @Override
    public List<Booking> getBookingsByStylist(User stylist) {
        return bookingRepository.findByStylist(stylist);
    }

    @Override
    public List<Booking> getBookingsByStatus(Booking.Status status) {
        return bookingRepository.findByStatus(status);
    }

    @Override
    public List<Booking> getBookingsByTimeRange(LocalDateTime from, LocalDateTime to) {
        return bookingRepository.findByBookingTimeBetween(from, to);
    }

    @Override
    public Booking saveBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    @Override
    public void deleteBooking(Integer id) {
        bookingRepository.deleteById(id);
    }
    @Override
public List<Booking> findAllForAdmin(String keyword, String status) {
    List<Booking> bookings = bookingRepository.findAll(); // Có thể custom query nếu data lớn
    return bookings.stream().filter(b -> {
        boolean ok = true;
        if (keyword != null && !keyword.isEmpty()) {
            String low = keyword.toLowerCase();
            ok = ok && (
                b.getCustomer().getFullName().toLowerCase().contains(low) ||
                b.getStylist().getFullName().toLowerCase().contains(low) ||
                b.getStatus().name().toLowerCase().contains(low)
            );
        }
        if (status != null && !status.isEmpty()) {
            ok = ok && b.getStatus().name().equalsIgnoreCase(status);
        }
        return ok;
    }).toList();
}

@Override
public void updateStatus(Integer id, Booking.Status status) {
    Booking booking = bookingRepository.findById(id).orElseThrow();
    booking.setStatus(status);
    bookingRepository.save(booking);
}

@Override
public Optional<Booking> getBookingById(Integer id) {
    return bookingRepository.findById(id);
}

}
