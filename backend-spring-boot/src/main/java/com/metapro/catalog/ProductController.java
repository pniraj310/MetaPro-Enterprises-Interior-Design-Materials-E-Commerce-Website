package com.metapro.catalog;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/{idOrSlug}")
    public ResponseEntity<Product> getProductByIdOrSlug(@PathVariable String idOrSlug) {
        return productRepository.findById(idOrSlug)
                .or(() -> productRepository.findBySlug(idOrSlug))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public Product createProduct(@RequestBody Product product) {
        if (product.getId() == null || product.getId().isBlank()) {
            product.setId("mp-prod-" + UUID.randomUUID().toString().substring(0, 8));
        }
        if (Boolean.TRUE.equals(product.getFeatured())) {
            productRepository.clearAllFeatured();
        }
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Product> updateProduct(@PathVariable String id, @RequestBody Product updated) {
        return productRepository.findById(id).map(existing -> {
            if (Boolean.TRUE.equals(updated.getFeatured())) {
                productRepository.clearAllFeatured();
            }
            existing.setName(updated.getName());
            existing.setSlug(updated.getSlug());
            existing.setDescription(updated.getDescription());
            existing.setCategory(updated.getCategory());
            existing.setPrice(updated.getPrice());
            existing.setSalePrice(updated.getSalePrice());
            existing.setSku(updated.getSku());
            existing.setStock(updated.getStock());
            existing.setUnit(updated.getUnit());
            existing.setImage(updated.getImage());
            existing.setAvailability(updated.getAvailability());
            existing.setFeatured(updated.getFeatured());
            existing.setUpdatedAt(LocalDateTime.now());
            return ResponseEntity.ok(productRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteProduct(@PathVariable String id) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("deleted", true));
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<Product> toggleAvailability(@PathVariable String id) {
        return productRepository.findById(id).map(existing -> {
            String next = "Available".equalsIgnoreCase(existing.getAvailability()) ? "Out of Stock" : "Available";
            existing.setAvailability(next);
            existing.setUpdatedAt(LocalDateTime.now());
            return ResponseEntity.ok(productRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }
}
