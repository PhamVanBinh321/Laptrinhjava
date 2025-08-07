package com.example.repository;


import com.example.model.StylistProfile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import com.example.model.StylistProfile.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;



public interface StylistProfileRepository extends JpaRepository<StylistProfile, Integer> {
    // Có thể custom thêm hàm search sau
    Optional<StylistProfile> findByUserId(Integer userId);

    // Tìm theo tên, level, specialties (có thể mở rộng)
    List<StylistProfile> findByUser_FullNameContainingIgnoreCaseAndLevel(String keyword, Level level);
    List<StylistProfile> findByUser_FullNameContainingIgnoreCase(String keyword);
    List<StylistProfile> findByLevel(Level level);
        // Tổng lương cố định của toàn bộ stylist (cấu hình trong bảng profile)
    @Query("SELECT SUM(sp.salary) FROM StylistProfile sp")
    Optional<BigDecimal> sumBaseSalary();
}
