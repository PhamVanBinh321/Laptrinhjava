package com.example.service;

import com.example.model.Feedback;
import com.example.model.User;
import com.example.repository.FeedbackRepository;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReviewWallService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    public record WallData(Page<Feedback> page, double avgRating, long totalReviews) {}

    public WallData load(int page, int size) {
        PageRequest pr = PageRequest.of(Math.max(page,0), Math.max(size,1), Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Feedback> p = feedbackRepository.findAllByOrderByCreatedAtDesc(pr);

        Object[] aggr = feedbackRepository.aggregateAll();
        double avg = 0.0; long cnt = 0L;
        if (aggr != null && aggr.length == 2) {
            Number a = (Number) aggr[0];
            Number c = (Number) aggr[1];
            avg = a == null ? 0.0 : a.doubleValue();
            cnt  = c == null ? 0L  : c.longValue();
        }
        return new WallData(p, avg, cnt);
    }

    @Transactional
    public void post(String username, int rating, String comment) {
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("Điểm phải từ 1 đến 5");
        if (comment == null || comment.trim().length() < 3) throw new IllegalArgumentException("Nhận xét quá ngắn");

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));

        Feedback fb = new Feedback();
        fb.setCustomer(user);
        fb.setRating(rating);
        fb.setComment(comment.trim());
        fb.setCreatedAt(LocalDateTime.now());
        // fb.setBooking(null); // để mặc định null, vì đây là tường đánh giá chung

        feedbackRepository.save(fb);
    }
}
