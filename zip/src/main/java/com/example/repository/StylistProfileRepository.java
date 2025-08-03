package com.example.repository;


import com.example.model.StylistProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StylistProfileRepository extends JpaRepository<StylistProfile, Integer> {
    // Có thể custom thêm hàm search sau
}
