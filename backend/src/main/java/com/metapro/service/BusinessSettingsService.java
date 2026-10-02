package com.metapro.service;

import com.metapro.model.BusinessSettings;
import com.metapro.repository.BusinessSettingsRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class BusinessSettingsService {

    private final BusinessSettingsRepository businessSettingsRepository;

    public BusinessSettingsService(BusinessSettingsRepository businessSettingsRepository) {
        this.businessSettingsRepository = businessSettingsRepository;
    }

    public BusinessSettings getSettings() {
        return businessSettingsRepository.findById("default_settings")
                .orElseGet(() -> {
                    BusinessSettings defaults = new BusinessSettings();
                    defaults.setId("default_settings");
                    return businessSettingsRepository.save(defaults);
                });
    }

    public BusinessSettings updateSettings(BusinessSettings newSettings) {
        BusinessSettings current = getSettings();
        if (newSettings.getBusinessName() != null) current.setBusinessName(newSettings.getBusinessName());
        if (newSettings.getWhatsAppNumber() != null) current.setWhatsAppNumber(newSettings.getWhatsAppNumber().replaceAll("[^0-9]", ""));
        if (newSettings.getBusinessPhone() != null) current.setBusinessPhone(newSettings.getBusinessPhone());
        if (newSettings.getBusinessEmail() != null) current.setBusinessEmail(newSettings.getBusinessEmail());
        if (newSettings.getBusinessAddress() != null) current.setBusinessAddress(newSettings.getBusinessAddress());
        if (newSettings.getHeroHeadline() != null) current.setHeroHeadline(newSettings.getHeroHeadline());
        if (newSettings.getHeroSubheadline() != null) current.setHeroSubheadline(newSettings.getHeroSubheadline());
        if (newSettings.getAboutText() != null) current.setAboutText(newSettings.getAboutText());
        if (newSettings.getInstagramUrl() != null) current.setInstagramUrl(newSettings.getInstagramUrl());
        if (newSettings.getFacebookUrl() != null) current.setFacebookUrl(newSettings.getFacebookUrl());
        if (newSettings.getYoutubeUrl() != null) current.setYoutubeUrl(newSettings.getYoutubeUrl());
        if (newSettings.getLinkedinUrl() != null) current.setLinkedinUrl(newSettings.getLinkedinUrl());
        if (newSettings.getFeaturedProductId() != null) current.setFeaturedProductId(newSettings.getFeaturedProductId());
        current.setUpdatedAt(Instant.now());
        return businessSettingsRepository.save(current);
    }
}
