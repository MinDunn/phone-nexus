package com.phonenexus.products.services.impl;

import com.phonenexus.products.models.Category;
import com.phonenexus.products.payload.request.CategoryRequest;
import com.phonenexus.products.payload.response.CategoryResponse;
import com.phonenexus.products.repositories.CategoryRepository;
import com.phonenexus.products.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    @Transactional
    @CacheEvict(value = { "categories", "root-categories", "sub-categories" }, allEntries = true)
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("Error: Category name is already taken!");
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Error: Parent category not found."));
            category.setParent(parent);
        }

        category = categoryRepository.save(category);
        return mapToResponse(category);
    }

    @Override
    @Transactional
    @CacheEvict(value = { "categories", "root-categories", "sub-categories" }, allEntries = true)
    public CategoryResponse updateCategory(UUID id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Category not found."));

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Error: Parent category not found."));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        category = categoryRepository.save(category);
        return mapToResponse(category);
    }

    @Override
    @Cacheable(value = "categories", key = "#id")
    public CategoryResponse getCategoryById(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Category not found."));
        return mapToResponse(category);
    }

    @Override
    @Cacheable(value = "root-categories")
    public List<CategoryResponse> getRootCategories() {
        return categoryRepository.findByParentIsNullAndIsDeletedFalse().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "sub-categories", key = "#parentId")
    public List<CategoryResponse> getSubCategories(UUID parentId) {
        return categoryRepository.findByParentIdAndIsDeletedFalse(parentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "categories", key = "'page-' + #pageable.pageNumber")
    public Page<CategoryResponse> getAllCategories(Pageable pageable) {
        return categoryRepository.findByIsDeletedFalse(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    @CacheEvict(value = { "categories", "root-categories", "sub-categories" }, allEntries = true)
    public void deleteCategory(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Category not found."));
        category.setDeleted(true);
        categoryRepository.save(category);
    }

    private CategoryResponse mapToResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getParent() != null ? category.getParent().getId() : null,
                category.getCreatedAt(),
                category.getUpdatedAt());
    }
}
