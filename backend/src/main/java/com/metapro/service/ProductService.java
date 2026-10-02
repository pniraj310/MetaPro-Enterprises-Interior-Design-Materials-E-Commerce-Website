package com.metapro.service;

import com.metapro.model.Product;
import com.metapro.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts(String category, String availability, String search) {
        List<Product> list = productRepository.findAll();

        if (category != null && !category.equalsIgnoreCase("All") && !category.isBlank()) {
            list = list.stream()
                    .filter(p -> p.getCategory() != null && p.getCategory().equalsIgnoreCase(category))
                    .collect(Collectors.toList());
        }

        if (availability != null && !availability.equalsIgnoreCase("all") && !availability.isBlank()) {
            list = list.stream()
                    .filter(p -> p.getAvailability() != null && p.getAvailability().equalsIgnoreCase(availability))
                    .collect(Collectors.toList());
        }

        if (search != null && !search.isBlank()) {
            String query = search.toLowerCase().trim();
            list = list.stream()
                    .filter(p -> (p.getName() != null && p.getName().toLowerCase().contains(query)) ||
                            (p.getDescription() != null && p.getDescription().toLowerCase().contains(query)) ||
                            (p.getCategory() != null && p.getCategory().toLowerCase().contains(query)) ||
                            (p.getSku() != null && p.getSku().toLowerCase().contains(query)))
                    .collect(Collectors.toList());
        }

        return list;
    }

    public Optional<Product> getProductByIdOrSlug(String idOrSlug) {
        Optional<Product> byId = productRepository.findById(idOrSlug);
        if (byId.isPresent()) {
            return byId;
        }
        return productRepository.findBySlug(idOrSlug);
    }

    public Product createProduct(Product product) {
        if (product.getSlug() == null || product.getSlug().isBlank()) {
            product.setSlug(generateSlug(product.getName()));
        }

        if (Boolean.TRUE.equals(product.getFeatured())) {
            unfeatureOthers(null);
        }

        product.setCreatedAt(Instant.now());
        product.setUpdatedAt(Instant.now());
        return productRepository.save(product);
    }

    public Optional<Product> updateProduct(String id, Product updatedData) {
        return productRepository.findById(id).map(existing -> {
            existing.setName(updatedData.getName());
            if (updatedData.getSlug() != null && !updatedData.getSlug().isBlank()) {
                existing.setSlug(updatedData.getSlug());
            } else {
                existing.setSlug(generateSlug(updatedData.getName()));
            }
            existing.setCategory(updatedData.getCategory());
            existing.setDescription(updatedData.getDescription());
            existing.setPrice(updatedData.getPrice());
            existing.setSalePrice(updatedData.getSalePrice());
            existing.setUnit(updatedData.getUnit());
            existing.setAvailability(updatedData.getAvailability());
            existing.setSku(updatedData.getSku());
            existing.setStock(updatedData.getStock());
            existing.setImage(updatedData.getImage());
            existing.setImages(updatedData.getImages());
            existing.setSpecifications(updatedData.getSpecifications());
            existing.setApplications(updatedData.getApplications());
            existing.setRelatedProductIds(updatedData.getRelatedProductIds());

            if (Boolean.TRUE.equals(updatedData.getFeatured())) {
                unfeatureOthers(id);
                existing.setFeatured(true);
            } else {
                existing.setFeatured(false);
            }

            if (updatedData.getActive() != null) {
                existing.setActive(updatedData.getActive());
            }

            existing.setUpdatedAt(Instant.now());
            return productRepository.save(existing);
        });
    }

    public Optional<Product> toggleAvailability(String id) {
        return productRepository.findById(id).map(p -> {
            String newStatus = "Available".equalsIgnoreCase(p.getAvailability()) ? "Out of Stock" : "Available";
            p.setAvailability(newStatus);
            p.setUpdatedAt(Instant.now());
            return productRepository.save(p);
        });
    }

    public Optional<Product> setFeaturedProduct(String id) {
        return productRepository.findById(id).map(p -> {
            unfeatureOthers(id);
            p.setFeatured(true);
            p.setUpdatedAt(Instant.now());
            return productRepository.save(p);
        });
    }

    public boolean deleteProduct(String id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void unfeatureOthers(String exceptId) {
        List<Product> featuredList = productRepository.findByFeaturedTrue();
        for (Product p : featuredList) {
            if (exceptId == null || !p.getId().equals(exceptId)) {
                p.setFeatured(false);
                productRepository.save(p);
            }
        }
    }

    private String generateSlug(String text) {
        if (text == null) return "product-" + System.currentTimeMillis();
        return text.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
    }
}
