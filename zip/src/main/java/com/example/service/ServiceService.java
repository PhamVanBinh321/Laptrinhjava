package com.example.service;


import com.example.model.Service;
import java.util.List;
import java.util.Optional;

public interface ServiceService {
    List<Service> getAllServices();
    Optional<Service> getServiceById(Integer id);
    List<Service> searchServices(String keyword);
    List<Service> getServicesByStatus(Service.Status status);
    Service saveService(Service service);
    void deleteService(Integer id);
     List<Service> searchServices(String keyword, String status);
}
