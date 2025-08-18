// src/main/java/com/example/web/dto/BookingForm.java
package com.example.dto;

import com.example.model.Booking;
import java.util.List;

public class BookingForm {
    private Integer stylistId;
    private List<Integer> serviceIds;
    private String startIso;
    private String endIso; // không bắt buộc, chủ yếu để UI
    private String note;
    private Booking.PaymentMethod paymentMethod = Booking.PaymentMethod.OFFLINE;
    private boolean usePoints;

    public Integer getStylistId() { return stylistId; }
    public void setStylistId(Integer stylistId) { this.stylistId = stylistId; }

    public List<Integer> getServiceIds() { return serviceIds; }
    public void setServiceIds(List<Integer> serviceIds) { this.serviceIds = serviceIds; }

    public String getStartIso() { return startIso; }
    public void setStartIso(String startIso) { this.startIso = startIso; }

    public String getEndIso() { return endIso; }
    public void setEndIso(String endIso) { this.endIso = endIso; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Booking.PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(Booking.PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public boolean isUsePoints() { return usePoints; }
    public void setUsePoints(boolean usePoints) { this.usePoints = usePoints; }
}
