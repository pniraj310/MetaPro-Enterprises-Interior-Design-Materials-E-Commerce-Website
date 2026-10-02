package com.metapro.repository;

import com.metapro.model.BusinessSettings;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessSettingsRepository extends MongoRepository<BusinessSettings, String> {
}
