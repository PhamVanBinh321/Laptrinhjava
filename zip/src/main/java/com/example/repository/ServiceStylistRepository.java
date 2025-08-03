package com.example.repository;


import com.example.model.ServiceStylist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceStylistRepository extends JpaRepository<ServiceStylist, ServiceStylist.ServiceStylistId> {
    // Có thể custom filter theo serviceId, stylistId
}
