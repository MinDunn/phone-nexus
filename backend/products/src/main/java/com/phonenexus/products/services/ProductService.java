package com.phonenexus.products.services;

import com.phonenexus.products.payload.request.ProductRequest;
import com.phonenexus.products.payload.request.ProductVariantRequest;
import com.phonenexus.products.payload.response.ProductResponse;
import com.phonenexus.products.payload.response.ProductVariantResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request, List<ProductVariantRequest> variantRequests);

    ProductResponse updateProduct(UUID id, ProductRequest request);

    ProductResponse getProductById(UUID id);

    Page<ProductResponse> getAllProducts(Pageable pageable);

    Page<ProductResponse> getProductsByCategory(UUID categoryId, Pageable pageable);

    Page<ProductResponse> getProductsByBrand(UUID brandId, Pageable pageable);

    Page<ProductResponse> searchProducts(String name, Pageable pageable);

    Page<ProductResponse> filterProducts(UUID brandId, UUID categoryId, java.math.BigDecimal minPrice,
            java.math.BigDecimal maxPrice, String name, Pageable pageable);

    void deleteProduct(UUID id);

    // Recently Viewed
    void logView(String userId, UUID productId);

    List<ProductResponse> getRecentlyViewed(String userId);

    // Variant Management
    ProductVariantResponse addVariant(UUID productId, ProductVariantRequest request);

    ProductVariantResponse updateVariant(UUID variantId, ProductVariantRequest request);

    void deleteVariant(UUID variantId);
}
