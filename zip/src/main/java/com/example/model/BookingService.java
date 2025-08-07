package com.example.model;


import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "booking_service")
@IdClass(BookingService.BookingServiceId.class)
public class BookingService {

    @Id
    @Column(name = "booking_id")
    private Integer bookingId;

    @Id
    @Column(name = "service_id")
    private Integer serviceId;

    // Quan hệ tới Booking
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", insertable = false, updatable = false)
    private Booking booking;

    // Quan hệ tới Service
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", insertable = false, updatable = false)
    private Service service;

    private Double price;   // Giá tại thời điểm đặt

    private Integer duration; // Thời lượng tại thời điểm đặt

    // ----- IDClass cho khóa phức hợp -----
    public static class BookingServiceId implements Serializable {
        private Integer bookingId;
        private Integer serviceId;

        public BookingServiceId() {}

        public BookingServiceId(Integer bookingId, Integer serviceId) {
            this.bookingId = bookingId;
            this.serviceId = serviceId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BookingServiceId)) return false;
            BookingServiceId that = (BookingServiceId) o;
            return Objects.equals(bookingId, that.bookingId) &&
                   Objects.equals(serviceId, that.serviceId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(bookingId, serviceId);
        }
    }

    // Constructors
    public BookingService() {}

    // Getters and Setters
    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
