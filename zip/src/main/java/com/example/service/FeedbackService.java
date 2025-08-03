package com.example.service;

import com.example.model.Feedback;
import com.example.model.User;
import java.util.List;
import java.util.Optional;

public interface FeedbackService {
    List<Feedback> getAllFeedback();
    Optional<Feedback> getById(Integer id);
    List<Feedback> getByCustomer(User customer);
    List<Feedback> getByRating(int rating);
    Feedback saveFeedback(Feedback feedback);
    void deleteFeedback(Integer id);
}
