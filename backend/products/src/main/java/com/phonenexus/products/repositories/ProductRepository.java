package com.phonenexus.products.repositories;

import com.phonenexus.products.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    Page<Product> findByIsDeletedFalse(Pageable pageable);

    Page<Product> findByCategoryIdAndIsDeletedFalse(UUID categoryId, Pageable pageable);

    Page<Product> findByBrandIdAndIsDeletedFalse(UUID brandId, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCaseAndIsDeletedFalse(String name, Pageable pageable);
}
