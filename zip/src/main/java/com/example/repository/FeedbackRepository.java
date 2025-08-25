package com.example.repository;


import com.example.model.Feedback;
import com.example.model.User;

import org.springframework.data.repository.query.Param;

import java.util.List;
import com.example.model.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    List<Feedback> findByCustomer_IdOrderByCreatedAtDesc(Integer customerId);
    List<Feedback> findByCustomer(User customer);
    List<Feedback> findByRating(int rating);
    // Thêm search/filter theo content nếu muốn
    /** tìm theo keyword + rating (rating = 0 nghĩa là không lọc) */
     /** Tìm kiếm theo keyword (tên KH hoặc nội dung) + rating (0 = bỏ lọc) */
    @Query("""
        SELECT f
        FROM Feedback f
             JOIN FETCH f.customer c
        WHERE (:kw IS NULL OR lower(c.fullName)   LIKE lower(concat('%',:kw,'%'))
                         OR lower(f.comment)      LIKE lower(concat('%',:kw,'%')))
          AND (:rate IS NULL OR f.rating = :rate)
        ORDER BY f.createdAt DESC
    """)
    List<Feedback> search(@Param("kw")    String  keyword,
                          @Param("rate")  Integer rating);
            
     @EntityGraph(attributePaths = {"customer"})                      
     Page<Feedback> findAllByOrderByCreatedAtDesc(Pageable pageable);

     @Query("SELECT AVG(COALESCE(f.rating, 0)) FROM Feedback f WHERE f.rating IS NOT NULL")
    Double findAverageRating();
    
    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.rating IS NOT NULL")
    Long countWithRating();

}
