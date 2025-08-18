package com.example.repository;


import com.example.model.Booking;
import com.example.model.User;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

import java.math.BigDecimal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface BookingRepository extends JpaRepository<Booking, Integer> {
     
      

      List<Booking> findByStylist_IdAndBookingTimeBetween(
        Integer stylistId, LocalDateTime start, LocalDateTime end
    );

       List<Booking> findByCustomer(User customer);
    List<Booking> findByStylist(User stylist);
    List<Booking> findByStatus(Booking.Status status);
    List<Booking> findByBookingTimeBetween(LocalDateTime start, LocalDateTime end);
    // Thêm hàm filter theo ngày, trạng thái, stylist, customer...
    /* Tổng doanh thu của từng stylist trong [start,end] và trạng thái DONE 
    @Query("""
           SELECT b.stylist.id,
                  SUM(b.totalAmount)
           FROM   Booking b
           WHERE  b.status = com.example.model.Booking.Status.DONE
             AND  b.bookingTime BETWEEN :start AND :end
           GROUP  BY b.stylist.id
           """)
    List<Object[]> sumRevenueGroupByStylist(@Param("start") LocalDateTime start,
                                            @Param("end")   LocalDateTime end);
                                             Tổng doanh thu (DONE) */
    @Query("""
        SELECT COALESCE(SUM(b.totalAmount), 0)
        FROM   Booking b
        WHERE  b.status = com.example.model.Booking.Status.DONE
          AND  b.bookingTime BETWEEN :start AND :end
    """)
    Double sumRevenue(@Param("start") LocalDateTime start,
                      @Param("end")   LocalDateTime end);

    /* Doanh thu group-by stylist */
    @Query("""
        SELECT b.stylist.id,
               COALESCE(SUM(b.totalAmount), 0)
        FROM   Booking b
        WHERE  b.status = com.example.model.Booking.Status.DONE
          AND  b.bookingTime BETWEEN :start AND :end
        GROUP BY b.stylist.id
    """)
    List<Object[]> sumRevenueGroupByStylist(@Param("start") LocalDateTime start,
                                            @Param("end")   LocalDateTime end);

    /* Thống kê dịch vụ */
    @Query("""
        SELECT s.name,
               COUNT(bs.id),
               COALESCE(SUM(bs.price), 0)
        FROM   BookingService bs
               JOIN bs.service s
               JOIN bs.booking b
        WHERE  b.status = com.example.model.Booking.Status.DONE
          AND  b.bookingTime BETWEEN :start AND :end
        GROUP BY s.id, s.name
        ORDER BY SUM(bs.price) DESC
    """)
    List<Object[]> serviceStats(@Param("start") LocalDateTime start,
                                @Param("end")   LocalDateTime end);

    /* Top khách hàng */
    @Query("""
        SELECT c.fullName,
               c.email,
               COUNT(b.id),
               COALESCE(SUM(b.totalAmount), 0)
        FROM   Booking b
               JOIN b.customer c
        WHERE  b.status = com.example.model.Booking.Status.DONE
          AND  b.bookingTime BETWEEN :start AND :end
        GROUP BY c.id, c.fullName, c.email
        ORDER BY SUM(b.totalAmount) DESC
    """)
    List<Object[]> topCustomers(@Param("start") LocalDateTime start,
                                @Param("end")   LocalDateTime end);

        List<Booking> findByCustomer_IdOrderByBookingTimeDesc(Integer customerId);
}
