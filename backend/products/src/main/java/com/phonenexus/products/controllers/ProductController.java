package com.phonenexus.products.controllers;

import com.phonenexus.products.payload.request.ProductRequest;
import com.phonenexus.products.payload.request.ProductVariantRequest;
import com.phonenexus.products.payload.response.ProductResponse;
import com.phonenexus.products.payload.response.ProductVariantResponse;
import com.phonenexus.products.services.ProductService;
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
@RequestMapping("/api/v1/products")
@Tag(name = "Product Management", description = "APIs for managing products and their variants")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    @Operation(summary = "Create a new product with initial variants")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest productRequest,
            @Valid @RequestBody List<ProductVariantRequest> variantRequests) {
        return ResponseEntity.ok(productService.createProduct(productRequest, variantRequests));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product core information")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable UUID id,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product detail with all variants by ID")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        if (userId != null) {
            productService.logView(userId, id);
        }
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recently viewed products (Limit 10)")
    public ResponseEntity<List<ProductResponse>> getRecentlyViewed(
            @RequestHeader(value = "X-User-Id", required = true) String userId) {
        return ResponseEntity.ok(productService.getRecentlyViewed(userId));
    }

    @GetMapping
    @Operation(summary = "Get all products with pagination")
    public ResponseEntity<Page<ProductResponse>> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get products by category")
    public ResponseEntity<Page<ProductResponse>> getProductsByCategory(@PathVariable UUID categoryId,
            Pageable pageable) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId, pageable));
    }

    @GetMapping("/brand/{brandId}")
    @Operation(summary = "Get products by brand")
    public ResponseEntity<Page<ProductResponse>> getProductsByBrand(@PathVariable UUID brandId, Pageable pageable) {
        return ResponseEntity.ok(productService.getProductsByBrand(brandId, pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "Search products by name")
    public ResponseEntity<Page<ProductResponse>> searchProducts(@RequestParam String q, Pageable pageable) {
        return ResponseEntity.ok(productService.searchProducts(q, pageable));
    }

    @GetMapping("/filter")
    @Operation(summary = "Advanced filter for products")
    public ResponseEntity<Page<ProductResponse>> filterProducts(
            @RequestParam(required = false) UUID brandId,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) java.math.BigDecimal minPrice,
            @RequestParam(required = false) java.math.BigDecimal maxPrice,
            @RequestParam(required = false) String q,
            Pageable pageable) {
        return ResponseEntity.ok(productService.filterProducts(brandId, categoryId, minPrice, maxPrice, q, pageable));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a product and all its variants")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // --- Variant Endpoints ---

    @PostMapping("/{productId}/variants")
    @Operation(summary = "Add a new variant to an existing product")
    public ResponseEntity<ProductVariantResponse> addVariant(@PathVariable UUID productId,
            @Valid @RequestBody ProductVariantRequest request) {
        return ResponseEntity.ok(productService.addVariant(productId, request));
    }

    @PutMapping("/variants/{variantId}")
    @Operation(summary = "Update an existing variant")
    public ResponseEntity<ProductVariantResponse> updateVariant(@PathVariable UUID variantId,
            @Valid @RequestBody ProductVariantRequest request) {
        return ResponseEntity.ok(productService.updateVariant(variantId, request));
    }

    @DeleteMapping("/variants/{variantId}")
    @Operation(summary = "Soft delete a variant")
    public ResponseEntity<Void> deleteVariant(@PathVariable UUID variantId) {
        productService.deleteVariant(variantId);
        return ResponseEntity.noContent().build();
    }
}
