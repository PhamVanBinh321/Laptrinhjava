package com.example.service;

import com.example.model.StylistProfile;
import java.util.List;
import java.util.Optional;

public interface StylistProfileService {
    List<StylistProfile> getAllStylistProfiles();
    Optional<StylistProfile> getById(Integer userId);
    StylistProfile saveStylistProfile(StylistProfile stylistProfile);
    void deleteStylistProfile(Integer userId);
}
