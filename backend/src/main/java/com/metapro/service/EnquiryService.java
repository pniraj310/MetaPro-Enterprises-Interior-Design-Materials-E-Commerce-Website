package com.metapro.service;

import com.metapro.model.Enquiry;
import com.metapro.repository.EnquiryRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class EnquiryService {

    private final EnquiryRepository enquiryRepository;

    public EnquiryService(EnquiryRepository enquiryRepository) {
        this.enquiryRepository = enquiryRepository;
    }

    public List<Enquiry> getAllEnquiries() {
        return enquiryRepository.findAllByOrderByCreatedAtDesc();
    }

    public Enquiry saveEnquiry(Enquiry enquiry) {
        enquiry.setCreatedAt(Instant.now());
        return enquiryRepository.save(enquiry);
    }
}
