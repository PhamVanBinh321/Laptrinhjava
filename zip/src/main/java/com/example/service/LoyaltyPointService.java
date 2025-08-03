package com.example.service;

import com.example.model.LoyaltyPoint;
import com.example.model.User;
import java.util.List;
import java.util.Optional;

public interface LoyaltyPointService {
    List<LoyaltyPoint> getAllPoints();
    Optional<LoyaltyPoint> getById(Integer id);
    List<LoyaltyPoint> getByCustomer(User customer);
    LoyaltyPoint savePoint(LoyaltyPoint point);
    void deletePoint(Integer id);
}
