package com.metapro.service;

import com.metapro.model.Category;
import com.metapro.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(String id) {
        return categoryRepository.findById(id);
    }

    public Category createCategory(Category category) {
        if (category.getSlug() == null || category.getSlug().isBlank()) {
            category.setSlug(category.getName().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-+|-+$", ""));
        }
        return categoryRepository.save(category);
    }

    public Optional<Category> updateCategory(String id, Category updatedData) {
        return categoryRepository.findById(id).map(existing -> {
            existing.setName(updatedData.getName());
            existing.setDescription(updatedData.getDescription());
            if (updatedData.getSlug() != null && !updatedData.getSlug().isBlank()) {
                existing.setSlug(updatedData.getSlug());
            }
            if (updatedData.getImage() != null) {
                existing.setImage(updatedData.getImage());
            }
            if (updatedData.getActive() != null) {
                existing.setActive(updatedData.getActive());
            }
            return categoryRepository.save(existing);
        });
    }

    public boolean deleteCategory(String id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
