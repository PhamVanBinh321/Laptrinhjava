package com.example.service.impl;

import com.example.model.Feedback;
import com.example.model.User;
import com.example.repository.FeedbackRepository;
import com.example.service.FeedbackService;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;

    @Autowired
    public FeedbackServiceImpl(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }
    
    @Override
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }
     

    @Override
    public List<Feedback> search(String keyword, Integer rating) {
        // truyền NULL khi người dùng không chọn
        String   kw  = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        Integer  rt  = (rating  == null || rating  == 0)      ? null : rating;
        return feedbackRepository.search(kw, rt);
    }


     @Override @Transactional
    public void reply(Integer id,String adminReply){
        Feedback fb = feedbackRepository.findById(id).orElseThrow();
        fb.setReply(adminReply);
        fb.setCreatedAt(LocalDateTime.now());   // hoặc thêm field repliedAt riêng
    }
    @Override
    public Optional<Feedback> getById(Integer id) {
        return feedbackRepository.findById(id);
    }

    @Override
    public List<Feedback> getByCustomer(User customer) {
        return feedbackRepository.findByCustomer(customer);
    }

    @Override
    public List<Feedback> getByRating(int rating) {
        return feedbackRepository.findByRating(rating);
    }

    @Override
    public Feedback saveFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    @Override
    public void deleteFeedback(Integer id) {
        feedbackRepository.deleteById(id);
    }
}
