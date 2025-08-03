package com.example.repository;


import com.example.model.LoyaltyPoint;
import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoyaltyPointRepository extends JpaRepository<LoyaltyPoint, Integer> {
    List<LoyaltyPoint> findByCustomer(User customer);
    // Tìm theo loại điểm, tổng điểm, v.v. có thể thêm sau
}
