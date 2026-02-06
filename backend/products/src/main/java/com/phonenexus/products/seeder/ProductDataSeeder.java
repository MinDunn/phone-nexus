package com.phonenexus.products.seeder;

import com.phonenexus.products.models.Brand;
import com.phonenexus.products.models.Category;
import com.phonenexus.products.models.Product;
import com.phonenexus.products.models.ProductVariant;
import com.phonenexus.products.repositories.BrandRepository;
import com.phonenexus.products.repositories.CategoryRepository;
import com.phonenexus.products.repositories.ProductRepository;
import com.phonenexus.products.repositories.ProductVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;

@Component
public class ProductDataSeeder implements CommandLineRunner {
        private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProductDataSeeder.class);

        @Autowired
        private BrandRepository brandRepository;
        @Autowired
        private CategoryRepository categoryRepository;
        @Autowired
        private ProductRepository productRepository;
        @Autowired
        private ProductVariantRepository variantRepository;

        @Override
        public void run(String... args) throws Exception {
                if (brandRepository.count() > 0) {
                        return;
                }

                // Brands
                Brand apple = Brand.builder().name("Apple").description("Think Different").logoUrl("logo_apple.png")
                                .build();
                Brand samsung = Brand.builder().name("Samsung").description("Imagine").logoUrl("logo_samsung.png")
                                .build();
                Brand xiaomi = Brand.builder().name("Xiaomi").description("Innovation for everyone")
                                .logoUrl("logo_xiaomi.png")
                                .build();
                brandRepository.saveAll(Arrays.asList(apple, samsung, xiaomi));

                // Categories
                Category phone = Category.builder().name("Dien thoai").description("Smartphone").build();
                Category tablet = Category.builder().name("May tinh bang").description("Tablet").build();
                categoryRepository.saveAll(Arrays.asList(phone, tablet));

                // Products
                Product iphone15 = Product.builder().name("iPhone 15 Pro Max").description("Titanium").brand(apple)
                                .category(phone).build();
                Product galaxyS24 = Product.builder().name("Samsung Galaxy S24 Ultra").description("Galaxy AI")
                                .brand(samsung)
                                .category(phone).build();
                productRepository.saveAll(Arrays.asList(iphone15, galaxyS24));

                // Variants
                ProductVariant v1 = ProductVariant.builder()
                                .product(iphone15)
                                .sku("IP15PM-256-NAT")
                                .color("Natural Titanium")
                                .storageCapacity("256GB")
                                .ram("8GB")
                                .stockQuantity(100)
                                .price(new BigDecimal("34990000"))
                                .imageUrl("ip15pm_nat.png")
                                .build();

                ProductVariant v2 = ProductVariant.builder()
                                .product(galaxyS24)
                                .sku("S24U-512-GRY")
                                .color("Titanium Gray")
                                .storageCapacity("512GB")
                                .ram("12GB")
                                .stockQuantity(50)
                                .price(new BigDecimal("33990000"))
                                .imageUrl("s24u_gry.png")
                                .build();

                // Note: We need to use addVariant methods or save manually if cascading is not
                // fully set for bi-directional
                // In our Product entity we have CascadeType.ALL, so saving Product with
                // variants added should work,
                // OR saving variants with product set. Since we saved products first, we set
                // product in variant and save variant.
                // Actually best practice with bi-directional is to set both sides.

                // However, since we already saved products, let's just save variants which
                // reference the products.
                variantRepository.saveAll(Arrays.asList(v1, v2));

                log.info(">>> SEED DATA INSERTED SUCCESSFULLY <<<");
        }
}
