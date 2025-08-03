package com.example.repository;


import com.example.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Integer> {
    List<Service> findByStatus(Service.Status status);
    List<Service> findByNameContainingIgnoreCase(String keyword);
    // Thêm các hàm filter khi cần
}
