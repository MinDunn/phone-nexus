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
import com.phonenexus.products.models.ItemStatus;
import com.phonenexus.products.models.ProductItem;
import com.phonenexus.products.repositories.ProductItemRepository;
import com.phonenexus.products.payload.request.BatchImeiImportRequest;
import com.phonenexus.products.payload.response.ProductItemResponse;
import com.phonenexus.products.exceptions.ResourceNotFoundException;
import com.phonenexus.products.repositories.RecentlyViewedProductRepository;
import com.phonenexus.products.services.ProductService;
import com.phonenexus.products.events.StockEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
        private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProductServiceImpl.class);

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

        @Autowired
        private ProductItemRepository productItemRepository;

        @Autowired
        private RabbitTemplate rabbitTemplate;

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
                product.incrementViewCount();
                productRepository.save(product);
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
                                .imageUrl(request.getImageUrl())
                                .build();

                product.addVariant(variant);
                variantRepository.save(variant);
                return mapVariantToResponse(variant);
        }

        @Override
        @Transactional
        @CacheEvict(value = "products", key = "#variantRepository.findById(#variantId).get().product.id")
        public ProductVariantResponse updateVariant(UUID variantId, ProductVariantRequest request) {
                ProductVariant variant = variantRepository.findById(variantId)
                                .orElseThrow(() -> new RuntimeException("Error: Variant not found."));

                // Check for Price or Stock changes
                boolean priceChanged = request.getPrice() != null
                                && variant.getPrice().compareTo(request.getPrice()) != 0;
                if (priceChanged) {
                        ProductHistory history = ProductHistory.builder()
                                        .productId(variant.getProduct().getId())
                                        .variantId(variant.getId())
                                        .sku(variant.getSku())
                                        .oldPrice(variant.getPrice())
                                        .newPrice(request.getPrice())
                                        .actionType("UPDATE_PRICE")
                                        .changedAt(LocalDateTime.now())
                                        .changedBy("SYSTEM")
                                        .build();
                        historyRepository.save(history);
                }

                variant.setSku(request.getSku());
                variant.setColor(request.getColor());
                variant.setStorageCapacity(request.getStorageCapacity());
                variant.setRam(request.getRam());
                variant.setPrice(request.getPrice());
                variant.setImageUrl(request.getImageUrl());

                variant = variantRepository.save(variant);
                return mapVariantToResponse(variant);
        }

        @Override
        @Transactional
        @CacheEvict(value = "products", key = "#variantRepository.findById(#variantId).get().product.id")
        public void deleteVariant(UUID variantId) {
                ProductVariant variant = variantRepository.findById(variantId)
                                .orElseThrow(() -> new RuntimeException("Error: Variant not found."));
                variant.setDeleted(true);
                variantRepository.save(variant);
        }

        @Override
        @Transactional
        @CacheEvict(value = "products", key = "#variantRepository.findById(#variantId).get().product.id")
        public void reduceStock(UUID variantId, Integer quantity) {
                // Logic moved to warehouse-service
        }

        private void publishStockAlert(ProductVariant variant) {
                try {
                        StockEvent event = new StockEvent(
                                        variant.getId(),
                                        variant.getSku(),
                                        variant.getProduct().getName(),
                                        0); // Threshold check moved to warehouse

                        rabbitTemplate.convertAndSend(
                                        com.phonenexus.products.config.RabbitMQConfig.EXCHANGE,
                                        com.phonenexus.products.config.RabbitMQConfig.ADMIN_ROUTING_KEY,
                                        event);
                        // log.info("Published low stock alert for variant: {}", variant.getSku());
                } catch (Exception e) {
                        log.error("Failed to publish stock alert for variant: {}", variant.getSku(), e);
                }
        }

        @Override
        @Transactional
        @CacheEvict(value = "products", key = "#variantRepository.findByIdWithLock(#variantId).get().product.id")
        public void increaseStock(UUID variantId, Integer quantity) {
                // Logic moved to warehouse-service
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

        @Override
        public List<ProductResponse> getPopularProducts() {
                return productRepository.findTop10ByIsDeletedFalseOrderByViewCountDesc().stream()
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
                                variant.getImageUrl());
        }

        @Override
        @Transactional
        public List<ProductItemResponse> importItems(UUID variantId, BatchImeiImportRequest request) {
                ProductVariant variant = variantRepository.findById(variantId)
                                .orElseThrow(() -> new ResourceNotFoundException("Variant not found: " + variantId));

                List<ProductItem> items = request.getImeis().stream()
                                .map(imei -> {
                                        if (productItemRepository.findByImei(imei).isPresent()) {
                                                throw new RuntimeException(
                                                                "IMEI " + imei + " already exists in system");
                                        }
                                        return ProductItem.builder()
                                                        .variant(variant)
                                                        .imei(imei)
                                                        .costPrice(request.getCostPrice())
                                                        .status(ItemStatus.AVAILABLE)
                                                        .build();
                                })
                                .collect(Collectors.toList());

                List<ProductItem> savedItems = productItemRepository.saveAll(items);

                // Update stock quantity automatically - Now handled via events to warehouse
                // service
                // variant.setStockQuantity(variant.getStockQuantity() + savedItems.size());
                // variantRepository.save(variant);

                return savedItems.stream()
                                .map(item -> new ProductItemResponse(
                                                item.getId(),
                                                item.getImei(),
                                                item.getCostPrice(),
                                                item.getStatus(),
                                                item.getCreatedAt(),
                                                item.getSoldAt()))
                                .collect(Collectors.toList());
        }

        @Override
        public List<ProductItemResponse> getItemsByVariant(UUID variantId) {
                return productItemRepository.findByVariantIdAndStatus(variantId, ItemStatus.AVAILABLE).stream()
                                .map(item -> new ProductItemResponse(
                                                item.getId(),
                                                item.getImei(),
                                                item.getCostPrice(),
                                                item.getStatus(),
                                                item.getCreatedAt(),
                                                item.getSoldAt()))
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional
        public void updateItemStatus(UUID itemId, ItemStatus status) {
                ProductItem item = productItemRepository.findById(itemId)
                                .orElseThrow(() -> new ResourceNotFoundException("Item not found: " + itemId));
                item.setStatus(status);
                if (status == ItemStatus.SOLD) {
                        item.setSoldAt(LocalDateTime.now());
                }
                productItemRepository.save(item);
        }

        @Override
        @Transactional
        public void updateItemStatusByImei(String imei, ItemStatus status) {
                ProductItem item = productItemRepository.findByImei(imei)
                                .orElseThrow(() -> new ResourceNotFoundException("Item not found with IMEI: " + imei));
                item.setStatus(status);
                if (status == ItemStatus.SOLD) {
                        item.setSoldAt(LocalDateTime.now());
                }
                productItemRepository.save(item);
        }
}
