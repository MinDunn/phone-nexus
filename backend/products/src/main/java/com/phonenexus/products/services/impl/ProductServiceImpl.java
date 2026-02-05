package com.phonenexus.products.services.impl;

import com.phonenexus.products.models.Brand;
import com.phonenexus.products.models.Category;
import com.phonenexus.products.models.Product;
import com.phonenexus.products.models.ProductVariant;
import com.phonenexus.products.payload.request.ProductRequest;
import com.phonenexus.products.payload.request.ProductVariantRequest;
import com.phonenexus.products.payload.response.ProductResponse;
import com.phonenexus.products.payload.response.ProductVariantResponse;
import com.phonenexus.products.repositories.BrandRepository;
import com.phonenexus.products.repositories.CategoryRepository;
import com.phonenexus.products.repositories.ProductRepository;
import com.phonenexus.products.repositories.ProductVariantRepository;
import com.phonenexus.products.repositories.ProductHistoryRepository;
import com.phonenexus.products.repositories.specs.ProductSpecification;
import com.phonenexus.products.models.ProductHistory;
import com.phonenexus.products.models.RecentlyViewedProduct;
import com.phonenexus.products.repositories.RecentlyViewedProductRepository;
import com.phonenexus.products.services.ProductService;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

        @Autowired
        private ProductRepository productRepository;

        @Autowired
        private ProductVariantRepository variantRepository;

        @Autowired
        private ProductHistoryRepository historyRepository;

        @Autowired
        private RecentlyViewedProductRepository recentlyViewedRepository;

        @Autowired
        private BrandRepository brandRepository;

        @Autowired
        private CategoryRepository categoryRepository;

        @Override
        @Transactional
        @CacheEvict(value = { "products", "products-by-category", "products-by-brand" }, allEntries = true)
        public ProductResponse createProduct(ProductRequest request, List<ProductVariantRequest> variantRequests) {
                Brand brand = brandRepository.findById(request.getBrandId())
                                .orElseThrow(() -> new RuntimeException("Error: Brand not found."));
                Category category = categoryRepository.findById(request.getCategoryId())
                                .orElseThrow(() -> new RuntimeException("Error: Category not found."));

                Product product = Product.builder()
                                .name(request.getName())
                                .description(request.getDescription())
                                .brand(brand)
                                .category(category)
                                .build();

                for (ProductVariantRequest vr : variantRequests) {
                        ProductVariant variant = ProductVariant.builder()
                                        .sku(vr.getSku())
                                        .color(vr.getColor())
                                        .storageCapacity(vr.getStorageCapacity())
                                        .ram(vr.getRam())
                                        .price(vr.getPrice())
                                        .stockQuantity(vr.getStockQuantity())
                                        .imageUrl(vr.getImageUrl())
                                        .build();
                        product.addVariant(variant);
                }

                product = productRepository.save(product);
                return mapToResponse(product);
        }

        @Override
        @Transactional
        @CacheEvict(value = { "products", "products-by-category", "products-by-brand" }, allEntries = true)
        public ProductResponse updateProduct(UUID id, ProductRequest request) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Error: Product not found."));

                Brand brand = brandRepository.findById(request.getBrandId())
                                .orElseThrow(() -> new RuntimeException("Error: Brand not found."));
                Category category = categoryRepository.findById(request.getCategoryId())
                                .orElseThrow(() -> new RuntimeException("Error: Category not found."));

                product.setName(request.getName());
                product.setDescription(request.getDescription());
                product.setBrand(brand);
                product.setCategory(category);

                product = productRepository.save(product);
                return mapToResponse(product);
        }

        @Override
        @Cacheable(value = "products", key = "#id")
        public ProductResponse getProductById(UUID id) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Error: Product not found."));
                return mapToResponse(product);
        }

        @Override
        @Cacheable(value = "products", key = "'page-' + #pageable.pageNumber")
        public Page<ProductResponse> getAllProducts(Pageable pageable) {
                return productRepository.findByIsDeletedFalse(pageable)
                                .map(this::mapToResponse);
        }

        @Override
        @Cacheable(value = "products-by-category", key = "#categoryId + '-' + #pageable.pageNumber")
        public Page<ProductResponse> getProductsByCategory(UUID categoryId, Pageable pageable) {
                return productRepository.findByCategoryIdAndIsDeletedFalse(categoryId, pageable)
                                .map(this::mapToResponse);
        }

        @Override
        @Cacheable(value = "products-by-brand", key = "#brandId + '-' + #pageable.pageNumber")
        public Page<ProductResponse> getProductsByBrand(UUID brandId, Pageable pageable) {
                return productRepository.findByBrandIdAndIsDeletedFalse(brandId, pageable)
                                .map(this::mapToResponse);
        }

        @Override
        public Page<ProductResponse> searchProducts(String name, Pageable pageable) {
                return productRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(name, pageable)
                                .map(this::mapToResponse);
        }

        @Override
        @Cacheable(value = "products", key = "'filter-' + #brandId + '-' + #categoryId + '-' + #minPrice + '-' + #maxPrice + '-' + #name + '-' + #pageable.pageNumber")
        public Page<ProductResponse> filterProducts(UUID brandId, UUID categoryId, java.math.BigDecimal minPrice,
                        java.math.BigDecimal maxPrice, String name, Pageable pageable) {
                return productRepository
                                .findAll(ProductSpecification.filterProducts(brandId, categoryId, minPrice, maxPrice,
                                                name), pageable)
                                .map(this::mapToResponse);
        }

        @Override
        @Transactional
        @CacheEvict(value = { "products", "products-by-category", "products-by-brand" }, allEntries = true)
        public void deleteProduct(UUID id) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Error: Product not found."));
                product.setDeleted(true);
                product.getVariants().forEach(v -> v.setDeleted(true));
                productRepository.save(product);
        }

        @Override
        @Transactional
        @CacheEvict(value = "products", key = "#productId")
        public ProductVariantResponse addVariant(UUID productId, ProductVariantRequest request) {
                Product product = productRepository.findById(productId)
                                .orElseThrow(() -> new RuntimeException("Error: Product not found."));

                ProductVariant variant = ProductVariant.builder()
                                .sku(request.getSku())
                                .color(request.getColor())
                                .storageCapacity(request.getStorageCapacity())
                                .ram(request.getRam())
                                .price(request.getPrice())
                                .stockQuantity(request.getStockQuantity())
                                .imageUrl(request.getImageUrl())
                                .build();

                product.addVariant(variant);
                variantRepository.save(variant);
                return mapVariantToResponse(variant);
        }

        @Override
        @Transactional
        public ProductVariantResponse updateVariant(UUID variantId, ProductVariantRequest request) {
                ProductVariant variant = variantRepository.findById(variantId)
                                .orElseThrow(() -> new RuntimeException("Error: Variant not found."));

                // Check for Price or Stock changes
                boolean priceChanged = request.getPrice() != null
                                && variant.getPrice().compareTo(request.getPrice()) != 0;
                boolean stockChanged = request.getStockQuantity() != null
                                && !variant.getStockQuantity().equals(request.getStockQuantity());

                if (priceChanged || stockChanged) {
                        ProductHistory history = ProductHistory.builder()
                                        .productId(variant.getProduct().getId())
                                        .variantId(variant.getId())
                                        .sku(variant.getSku())
                                        .oldPrice(variant.getPrice())
                                        .newPrice(priceChanged ? request.getPrice() : variant.getPrice())
                                        .oldStock(variant.getStockQuantity())
                                        .newStock(stockChanged ? request.getStockQuantity()
                                                        : variant.getStockQuantity())
                                        .actionType(priceChanged && stockChanged ? "UPDATE_PRICE_STOCK"
                                                        : (priceChanged ? "UPDATE_PRICE" : "UPDATE_STOCK"))
                                        .changedAt(LocalDateTime.now())
                                        .changedBy("SYSTEM") // Placeholder, ideally get from SecurityContext
                                        .build();
                        historyRepository.save(history);
                }

                variant.setSku(request.getSku());
                variant.setColor(request.getColor());
                variant.setStorageCapacity(request.getStorageCapacity());
                variant.setRam(request.getRam());
                variant.setPrice(request.getPrice());
                variant.setStockQuantity(request.getStockQuantity());
                variant.setImageUrl(request.getImageUrl());

                variant = variantRepository.save(variant);
                return mapVariantToResponse(variant);
        }

        @Override
        @Transactional
        public void deleteVariant(UUID variantId) {
                ProductVariant variant = variantRepository.findById(variantId)
                                .orElseThrow(() -> new RuntimeException("Error: Variant not found."));
                variant.setDeleted(true);
                variantRepository.save(variant);
        }

        @Override
        @Transactional
        public void logView(String userId, UUID productId) {
                if (userId == null)
                        return;

                Optional<RecentlyViewedProduct> existing = recentlyViewedRepository.findByUserIdAndProductId(userId,
                                productId);
                if (existing.isPresent()) {
                        existing.get().setViewedAt(LocalDateTime.now());
                        recentlyViewedRepository.save(existing.get());
                } else {
                        RecentlyViewedProduct view = RecentlyViewedProduct.builder()
                                        .userId(userId)
                                        .productId(productId)
                                        .viewedAt(LocalDateTime.now())
                                        .build();
                        recentlyViewedRepository.save(view);
                }
        }

        @Override
        public List<ProductResponse> getRecentlyViewed(String userId) {
                Pageable limit = PageRequest.of(0, 10); // Last 10 viewed
                List<RecentlyViewedProduct> views = recentlyViewedRepository.findByUserIdOrderByViewedAtDesc(userId,
                                limit);

                return views.stream()
                                .map(v -> productRepository.findById(v.getProductId()))
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        private ProductResponse mapToResponse(Product product) {
                List<ProductVariantResponse> variants = product.getVariants().stream()
                                .filter(v -> !v.isDeleted())
                                .map(this::mapVariantToResponse)
                                .collect(Collectors.toList());

                return new ProductResponse(
                                product.getId(),
                                product.getName(),
                                product.getDescription(),
                                product.getBrand().getId(),
                                product.getBrand().getName(),
                                product.getCategory().getId(),
                                product.getCategory().getName(),
                                product.getStatus().name(),
                                variants);
        }

        private ProductVariantResponse mapVariantToResponse(ProductVariant variant) {
                return new ProductVariantResponse(
                                variant.getId(),
                                variant.getSku(),
                                variant.getColor(),
                                variant.getStorageCapacity(),
                                variant.getRam(),
                                variant.getPrice(),
                                variant.getStockQuantity(),
                                variant.getImageUrl());
        }
}
