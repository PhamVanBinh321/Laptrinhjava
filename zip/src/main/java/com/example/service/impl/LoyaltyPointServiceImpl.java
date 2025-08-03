package com.example.service.impl;

import com.example.model.LoyaltyPoint;
import com.example.model.User;
import com.example.repository.LoyaltyPointRepository;
import com.example.service.LoyaltyPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LoyaltyPointServiceImpl implements LoyaltyPointService {

    private final LoyaltyPointRepository loyaltyPointRepository;

    @Autowired
    public LoyaltyPointServiceImpl(LoyaltyPointRepository loyaltyPointRepository) {
        this.loyaltyPointRepository = loyaltyPointRepository;
    }

    @Override
    public List<LoyaltyPoint> getAllPoints() {
        return loyaltyPointRepository.findAll();
    }

    @Override
    public Optional<LoyaltyPoint> getById(Integer id) {
        return loyaltyPointRepository.findById(id);
    }

    @Override
    public List<LoyaltyPoint> getByCustomer(User customer) {
        return loyaltyPointRepository.findByCustomer(customer);
    }

    @Override
    public LoyaltyPoint savePoint(LoyaltyPoint point) {
        return loyaltyPointRepository.save(point);
    }

    @Override
    public void deletePoint(Integer id) {
        loyaltyPointRepository.deleteById(id);
    }
}
