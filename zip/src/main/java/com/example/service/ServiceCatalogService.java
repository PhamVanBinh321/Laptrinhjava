// src/main/java/com/example/service/ServiceCatalogService.java
package com.example.service;

import com.example.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

// Dùng fully-qualified cho annotation để không va chạm với entity com.example.model.Service
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceCatalogService {

    private final ServiceRepository serviceRepository;

    public Page<com.example.model.Service> searchActive(String q, int page, int size, String sortBy) {
        Sort sort = switch (sortBy == null ? "name" : sortBy) {
            case "price" -> Sort.by(Sort.Direction.ASC, "price");
            case "duration" -> Sort.by(Sort.Direction.ASC, "duration");
            default -> Sort.by(Sort.Direction.ASC, "name");
        };
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sort);

        if (q == null || q.isBlank()) {
            return serviceRepository.findByStatus(com.example.model.Service.Status.ACTIVE, pageable);
        }
        return serviceRepository.findByStatusAndNameContainingIgnoreCase(
                com.example.model.Service.Status.ACTIVE, q.trim(), pageable
        );
    }

    public com.example.model.Service getActiveById(Integer id) {
        return serviceRepository.findByIdAndStatus(
                        id, com.example.model.Service.Status.ACTIVE
                )
                .orElseThrow(() ->
                        new java.util.NoSuchElementException("Service not found or inactive: " + id));
    }
}
