package com.example.service;

import com.example.model.StylistProfile;
import java.util.List;
import java.util.Optional;
import com.example.model.StylistProfile;
import com.example.repository.StylistProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

public interface StylistProfileService {
    List<StylistProfile> getAllStylistProfiles();
    Optional<StylistProfile> getById(Integer userId);
    StylistProfile saveStylistProfile(StylistProfile stylistProfile);
    void deleteStylistProfile(Integer userId);
       List<StylistProfile> searchStylists(String keyword, String level);
    Optional<StylistProfile> getByUserId(Integer userId);
    void saveStylist(StylistProfile stylist);
     public Page<StylistProfile> findPage(String q, String sort, int page, int size);
     
}
