package com.example.repository;


import com.example.model.Feedback;
import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    List<Feedback> findByCustomer(User customer);
    List<Feedback> findByRating(int rating);
    // Thêm search/filter theo content nếu muốn
}
