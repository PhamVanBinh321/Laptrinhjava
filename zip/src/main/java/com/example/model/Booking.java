package com.example.model;


import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Quan hệ với customer (User)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    // Quan hệ với stylist (User)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stylist_id", nullable = false)
    private User stylist;

    @Column(name = "booking_time", nullable = false)
    private LocalDateTime bookingTime;

    @Column(name = "total_amount", precision = 12, scale = 2)
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Status status = Status.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 10)
    private PaymentMethod paymentMethod = PaymentMethod.OFFLINE;

    @Lob
    private String note;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Quan hệ tới các dịch vụ đã đặt trong booking này (BookingService)
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookingService> bookingServices;

    // Enum cho status
    public enum Status {
        PENDING, CONFIRMED, CANCELLED, DONE, NO_SHOW
    }

    public enum PaymentMethod {
        OFFLINE, ONLINE
    }

    // Constructors
    public Booking() {}

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public User getStylist() {
        return stylist;
    }

    public void setStylist(User stylist) {
        this.stylist = stylist;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<BookingService> getBookingServices() {
        return bookingServices;
    }

    public void setBookingServices(List<BookingService> bookingServices) {
        this.bookingServices = bookingServices;
    }
}
