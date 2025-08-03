package com.example.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "service_stylist")
@IdClass(ServiceStylist.ServiceStylistId.class)
public class ServiceStylist {

    @Id
    @Column(name = "service_id")
    private Integer serviceId;

    @Id
    @Column(name = "stylist_id")
    private Integer stylistId;

    // Quan hệ ManyToOne tới Service
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", insertable = false, updatable = false)
    private Service service;

    // Quan hệ ManyToOne tới User (stylist)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stylist_id", insertable = false, updatable = false)
    private User stylist;

    // Constructor rỗng
    public ServiceStylist() {}

    // Getters and Setters
    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getStylistId() {
        return stylistId;
    }

    public void setStylistId(Integer stylistId) {
        this.stylistId = stylistId;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public User getStylist() {
        return stylist;
    }

    public void setStylist(User stylist) {
        this.stylist = stylist;
    }

    // --------- IDClass cho khóa chính phức hợp -----------
    public static class ServiceStylistId implements Serializable {
        private Integer serviceId;
        private Integer stylistId;

        public ServiceStylistId() {}

        public ServiceStylistId(Integer serviceId, Integer stylistId) {
            this.serviceId = serviceId;
            this.stylistId = stylistId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ServiceStylistId)) return false;
            ServiceStylistId that = (ServiceStylistId) o;
            return Objects.equals(serviceId, that.serviceId) && Objects.equals(stylistId, that.stylistId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(serviceId, stylistId);
        }
    }
}
