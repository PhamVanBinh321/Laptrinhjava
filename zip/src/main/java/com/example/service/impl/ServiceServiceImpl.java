package com.example.service.impl;


import com.example.model.Service;
import com.example.repository.ServiceRepository;
import com.example.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
// Removed alias import for Service annotation
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;

    @Autowired
    public ServiceServiceImpl(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    @Override
    public Optional<Service> getServiceById(Integer id) {
        return serviceRepository.findById(id);
    }

    @Override
    public List<Service> searchServices(String keyword) {
        return serviceRepository.findByNameContainingIgnoreCase(keyword);
    }

    @Override
    public List<Service> getServicesByStatus(Service.Status status) {
        return serviceRepository.findByStatus(status);
    }

    @Override
    public Service saveService(Service service) {
        return serviceRepository.save(service);
    }

    @Override
    public void deleteService(Integer id) {
        serviceRepository.deleteById(id);
    }
}
