package com.phonenexus.products.services;

import com.phonenexus.products.models.Brand;
import com.phonenexus.products.models.Category;
import com.phonenexus.products.models.Product;
import com.phonenexus.products.payload.request.ProductRequest;
import com.phonenexus.products.payload.request.ProductVariantRequest;
import com.phonenexus.products.payload.response.ProductResponse;
import com.phonenexus.products.repositories.BrandRepository;
import com.phonenexus.products.repositories.CategoryRepository;
import com.phonenexus.products.repositories.ProductRepository;
import com.phonenexus.products.services.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private BrandRepository brandRepository;
    @Mock
    private CategoryRepository categoryRepository;

    // We don't mock ProductVariantRepository for createProduct as it's cascaded or
    // not explicitly called in create
    // depending on implementation. In our impl, we save product which cascades
    // variants?
    // Checking impl: yes, product.addVariant(variant) then
    // productRepository.save(product).
    // wait, actually in createProduct we loop and add variants to product, then
    // save product.
    // So distinct variantRepository save is not called there.
    // However, if we need it for addVariant method, we should mock it.
    // For now, let's test createProduct.

    @InjectMocks
    private ProductServiceImpl productService;

    private Brand brand;
    private Category category;
    private ProductRequest productRequest;
    private List<ProductVariantRequest> variantRequests;

    @BeforeEach
    void setUp() {
        UUID brandId = UUID.randomUUID();
        brand = Brand.builder().name("Apple").build();
        // Reflection to set ID since it's private and no setter for ID usually (or
        // protected in BaseEntity)
        // For simple unit tests without reflection tools, we can rely on mocking or
        // just assume IDs are null or
        // add public setId for testing if strictly needed, but BaseEntity usually
        // doesn't have public setId.
        // Actually BaseEntity has generated ID. Mocking the repository response is key.
        // Let's assume we mock repository to return object with ID.

        UUID categoryId = UUID.randomUUID();
        category = Category.builder().name("Phone").build();

        productRequest = new ProductRequest();
        productRequest.setName("iPhone 15");
        productRequest.setDescription("New iPhone");
        productRequest.setBrandId(brandId);
        productRequest.setCategoryId(categoryId);

        ProductVariantRequest variantRequest = new ProductVariantRequest();
        variantRequest.setSku("SKU-1");
        variantRequest.setPrice(BigDecimal.valueOf(1000));
        variantRequest.setStockQuantity(10);
        variantRequests = Collections.singletonList(variantRequest);
    }

    @Test
    void testCreateProduct_Success() {
        // Arrange
        when(brandRepository.findById(any())).thenReturn(Optional.of(brand));
        when(categoryRepository.findById(any())).thenReturn(Optional.of(category));

        Product savedProduct = Product.builder()
                .name(productRequest.getName())
                .brand(brand)
                .category(category)
                .build();
        // Simulate ID generation
        // ideally we can't easily set ID on the object returned by save unless we use
        // reflection or a helper.
        // For this test, we accept ID might be null in response DTO if we don't mock it
        // fully,
        // but let's see.

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // Act
        ProductResponse response = productService.createProduct(productRequest, variantRequests);

        // Assert
        assertNotNull(response);
        assertEquals("iPhone 15", response.getName());
        assertEquals("Apple", response.getBrandName());
        assertEquals("Phone", response.getCategoryName());
        // Variants might be empty if save didn't cascade populate IDs or return them in
        // the mock
        // properly, but the logic should hold.
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testGetProductById_Success() {
        // Arrange
        UUID productId = UUID.randomUUID();
        Product product = Product.builder()
                .name("iPhone 15")
                .brand(brand)
                .category(category)
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Act
        ProductResponse response = productService.getProductById(productId);

        // Assert
        assertNotNull(response);
        assertEquals("iPhone 15", response.getName());
    }

    @Test
    void testGetProductById_NotFound() {
        // Arrange
        UUID productId = UUID.randomUUID();
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> productService.getProductById(productId));
    }
}
