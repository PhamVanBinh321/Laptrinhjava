package com.example.service.impl;

import com.example.model.StylistProfile;
import com.example.repository.StylistProfileRepository;
import com.example.service.StylistProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class StylistProfileServiceImpl implements StylistProfileService {

    private final StylistProfileRepository stylistProfileRepository;

    @Autowired
    public StylistProfileServiceImpl(StylistProfileRepository stylistProfileRepository) {
        this.stylistProfileRepository = stylistProfileRepository;
    }

    @Override
    public List<StylistProfile> getAllStylistProfiles() {
        return stylistProfileRepository.findAll();
    }

    @Override
    public Optional<StylistProfile> getById(Integer userId) {
        return stylistProfileRepository.findById(userId);
    }

    @Override
    public StylistProfile saveStylistProfile(StylistProfile stylistProfile) {
        return stylistProfileRepository.save(stylistProfile);
    }

    @Override
    public void deleteStylistProfile(Integer userId) {
        stylistProfileRepository.deleteById(userId);
    }
}
