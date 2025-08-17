package com.example.service.impl;

import com.example.model.StylistProfile;
import com.example.repository.ServiceRepository;
import com.example.repository.StylistProfileRepository;
import com.example.service.StylistProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import com.example.model.StylistProfile;
import com.example.repository.StylistProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
@Service
public class StylistProfileServiceImpl implements StylistProfileService {


        private final StylistProfileRepository stylistProfileRepository;

    public Page<StylistProfile> findPage(String q, String sort, int page, int size) {
        Sort s;
        if ("experience".equalsIgnoreCase(sort)) {
            s = Sort.by(Sort.Direction.DESC, "experienceYears")
                    .and(Sort.by(Sort.Direction.ASC, "user.fullName"))
                    .and(Sort.by(Sort.Direction.ASC, "user.username"));
        } else { // default: name
            s = Sort.by(Sort.Direction.ASC, "user.fullName")
                    .and(Sort.by(Sort.Direction.ASC, "user.username"));
        }
        Pageable pageable = PageRequest.of(Math.max(page,0), Math.max(size,1), s);
        return stylistProfileRepository.search(q, pageable);
    }

    @Override
    public List<StylistProfile> searchStylists(String keyword, String level) {
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasLevel = level != null && !level.trim().isEmpty();

        if (hasKeyword && hasLevel) {
            return stylistProfileRepository.findByUser_FullNameContainingIgnoreCaseAndLevel(
                    keyword.trim(), StylistProfile.Level.valueOf(level));
        } else if (hasKeyword) {
            return stylistProfileRepository.findByUser_FullNameContainingIgnoreCase(keyword.trim());
        } else if (hasLevel) {
            return stylistProfileRepository.findByLevel(StylistProfile.Level.valueOf(level));
        } else {
            return stylistProfileRepository.findAll();
        }
    }

    @Override
    public Optional<StylistProfile> getByUserId(Integer userId) {
        return stylistProfileRepository.findByUserId(userId);
    }

    @Override
    public void saveStylist(StylistProfile stylist) {
        stylistProfileRepository.save(stylist);
    }

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
