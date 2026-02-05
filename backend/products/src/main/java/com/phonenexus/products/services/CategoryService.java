package com.phonenexus.products.services;

import com.phonenexus.products.payload.request.CategoryRequest;
import com.phonenexus.products.payload.response.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse updateCategory(UUID id, CategoryRequest request);

    CategoryResponse getCategoryById(UUID id);

    List<CategoryResponse> getRootCategories();

    List<CategoryResponse> getSubCategories(UUID parentId);

    Page<CategoryResponse> getAllCategories(Pageable pageable);

    void deleteCategory(UUID id);
}
