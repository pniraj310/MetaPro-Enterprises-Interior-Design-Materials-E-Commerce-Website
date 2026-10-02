package com.metapro.repository;

import com.metapro.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    Optional<Product> findBySlug(String slug);
    List<Product> findByCategoryIgnoreCase(String category);
    List<Product> findByFeaturedTrue();
    List<Product> findByActiveTrue();
    List<Product> findByCategoryIgnoreCaseAndActiveTrue(String category);
}
