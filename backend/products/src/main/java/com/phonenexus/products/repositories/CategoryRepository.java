package com.phonenexus.products.repositories;

import com.phonenexus.products.models.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findByName(String name);

    List<Category> findByParentIsNullAndIsDeletedFalse();

    List<Category> findByParentIdAndIsDeletedFalse(UUID parentId);

    Page<Category> findByIsDeletedFalse(Pageable pageable);
}
