package com.example.repository;


import com.example.model.Service;

import java.util.List;
import java.util.Optional;

import com.example.model.Service;
import com.example.model.Service.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.model.Service;
import com.example.model.Service.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
public interface ServiceRepository extends JpaRepository<Service, Integer> {
    List<Service> findByStatus(Service.Status status);
    List<Service> findByNameContainingIgnoreCase(String keyword);
    // Thêm các hàm filter khi cần
     List<Service> findByNameContainingIgnoreCaseAndStatus(String keyword, Service.Status status);
      
          Page<Service> findByStatus(Status status, Pageable pageable);

    Page<Service> findByStatusAndNameContainingIgnoreCase(Status status, String name, Pageable pageable);

    Optional<Service> findByIdAndStatus(Integer id, Status status);
        @Query("""
        SELECT s FROM Service s
        WHERE (:q IS NULL OR :q = '' 
               OR LOWER(s.name)  LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(s.intro) LIKE LOWER(CONCAT('%', :q, '%')))
          AND (:status IS NULL OR s.status = :status)
        """)
    Page<Service> search(@Param("q") String q,
                         @Param("status") Status status,
                         Pageable pageable);
}
