package com.phonenexus.products.controllers;

import com.phonenexus.products.payload.request.CategoryRequest;
import com.phonenexus.products.payload.response.CategoryResponse;
import com.phonenexus.products.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products/categories")
@Tag(name = "Category Management", description = "APIs for managing product categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Create a new category")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.createCategory(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing category")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable UUID id,
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @GetMapping("/roots")
    @Operation(summary = "Get all root categories")
    public ResponseEntity<List<CategoryResponse>> getRootCategories() {
        return ResponseEntity.ok(categoryService.getRootCategories());
    }

    @GetMapping("/{parentId}/subs")
    @Operation(summary = "Get subcategories by parent ID")
    public ResponseEntity<List<CategoryResponse>> getSubCategories(@PathVariable UUID parentId) {
        return ResponseEntity.ok(categoryService.getSubCategories(parentId));
    }

    @GetMapping
    @Operation(summary = "Get all categories with pagination")
    public ResponseEntity<Page<CategoryResponse>> getAllCategories(Pageable pageable) {
        return ResponseEntity.ok(categoryService.getAllCategories(pageable));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a category")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
