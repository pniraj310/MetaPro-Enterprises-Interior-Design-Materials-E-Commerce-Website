package com.metapro.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "business_settings")
public class BusinessSettings {

    @Id
    private String id = "default_settings";

    private String businessName = "MetaPro Enterprises";
    private String whatsAppNumber = "917666323894";
    private String businessPhone = "7666323894";
    private String businessEmail = "pdheeraj351@gmail.com";
    private String businessAddress = "Pili Nadi, Sunday Market, Plot No. 02, Kamptee Road, Nagpur, Maharashtra 440026, India";
    private String heroHeadline = "Materials That Shape Better Spaces.";
    private String heroSubheadline = "Explore interior materials, architectural wall & ceiling panels, and precision fixing hardware from MetaPro Enterprises.";
    private String aboutText = "MetaPro Enterprises supplies interior materials, architectural wall and ceiling panels, and installation fasteners for customers working on homes, offices, commercial spaces, and interior renovation projects.";

    private String instagramUrl = "";
    private String facebookUrl = "";
    private String youtubeUrl = "";
    private String linkedinUrl = "";

    private String featuredProductId = "";

    private Instant updatedAt = Instant.now();

    public BusinessSettings() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getWhatsAppNumber() {
        return whatsAppNumber;
    }

    public void setWhatsAppNumber(String whatsAppNumber) {
        this.whatsAppNumber = whatsAppNumber;
    }

    public String getBusinessPhone() {
        return businessPhone;
    }

    public void setBusinessPhone(String businessPhone) {
        this.businessPhone = businessPhone;
    }

    public String getBusinessEmail() {
        return businessEmail;
    }

    public void setBusinessEmail(String businessEmail) {
        this.businessEmail = businessEmail;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public String getHeroHeadline() {
        return heroHeadline;
    }

    public void setHeroHeadline(String heroHeadline) {
        this.heroHeadline = heroHeadline;
    }

    public String getHeroSubheadline() {
        return heroSubheadline;
    }

    public void setHeroSubheadline(String heroSubheadline) {
        this.heroSubheadline = heroSubheadline;
    }

    public String getAboutText() {
        return aboutText;
    }

    public void setAboutText(String aboutText) {
        this.aboutText = aboutText;
    }

    public String getInstagramUrl() {
        return instagramUrl;
    }

    public void setInstagramUrl(String instagramUrl) {
        this.instagramUrl = instagramUrl;
    }

    public String getFacebookUrl() {
        return facebookUrl;
    }

    public void setFacebookUrl(String facebookUrl) {
        this.facebookUrl = facebookUrl;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public String getFeaturedProductId() {
        return featuredProductId;
    }

    public void setFeaturedProductId(String featuredProductId) {
        this.featuredProductId = featuredProductId;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
