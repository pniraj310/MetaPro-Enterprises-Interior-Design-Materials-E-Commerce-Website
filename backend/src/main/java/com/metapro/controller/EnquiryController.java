package com.metapro.controller;

import com.metapro.model.Enquiry;
import com.metapro.service.EnquiryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enquiries")
public class EnquiryController {

    private final EnquiryService enquiryService;

    public EnquiryController(EnquiryService enquiryService) {
        this.enquiryService = enquiryService;
    }

    @GetMapping
    public ResponseEntity<List<Enquiry>> getAllEnquiries() {
        return ResponseEntity.ok(enquiryService.getAllEnquiries());
    }

    @PostMapping
    public ResponseEntity<Enquiry> submitEnquiry(@RequestBody Enquiry enquiry) {
        Enquiry saved = enquiryService.saveEnquiry(enquiry);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
