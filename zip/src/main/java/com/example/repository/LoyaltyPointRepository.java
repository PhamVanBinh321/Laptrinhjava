package com.example.repository;


import com.example.model.LoyaltyPoint;
import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LoyaltyPointRepository extends JpaRepository<LoyaltyPoint, Integer> {
    List<LoyaltyPoint> findByCustomer(User customer);
    // Tìm theo loại điểm, tổng điểm, v.v. có thể thêm sau
      List<LoyaltyPoint> findByCustomer_IdOrderByCreatedAtDesc(Integer customerId);
       @Query("""
        select coalesce(sum(case when lp.type = com.example.model.LoyaltyPoint$Type.EARN
                                 then lp.points else -lp.points end), 0)
        from LoyaltyPoint lp
        where lp.customer.id = :cid
    """)
    Integer getBalance(@Param("cid") Integer customerId);
     boolean existsByCustomer_IdAndTypeAndDescription(Integer customerId,
                                                     LoyaltyPoint.Type type,
                                                     String description);
}
