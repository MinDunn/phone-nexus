package com.phonenexus.products.services.impl;

import com.phonenexus.products.models.Brand;
import com.phonenexus.products.payload.request.BrandRequest;
import com.phonenexus.products.payload.response.BrandResponse;
import com.phonenexus.products.repositories.BrandRepository;
import com.phonenexus.products.services.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Override
    @Transactional
    @CacheEvict(value = "brands", allEntries = true)
    public BrandResponse createBrand(BrandRequest request) {
        if (brandRepository.existsByName(request.getName())) {
            throw new RuntimeException("Error: Brand name is already taken!");
        }

        Brand brand = Brand.builder()
                .name(request.getName())
                .description(request.getDescription())
                .logoUrl(request.getLogoUrl())
                .build();

        brand = brandRepository.save(brand);
        return mapToResponse(brand);
    }

    @Override
    @Transactional
    @CacheEvict(value = "brands", allEntries = true)
    public BrandResponse updateBrand(UUID id, BrandRequest request) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Brand not found."));

        brand.setName(request.getName());
        brand.setDescription(request.getDescription());
        brand.setLogoUrl(request.getLogoUrl());

        brand = brandRepository.save(brand);
        return mapToResponse(brand);
    }

    @Override
    @Cacheable(value = "brands", key = "#id")
    public BrandResponse getBrandById(UUID id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Brand not found."));
        return mapToResponse(brand);
    }

    @Override
    @Cacheable(value = "brands", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<BrandResponse> getAllBrands(Pageable pageable) {
        return brandRepository.findByIsDeletedFalse(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    @CacheEvict(value = "brands", allEntries = true)
    public void deleteBrand(UUID id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Brand not found."));
        brand.setDeleted(true);
        brandRepository.save(brand);
    }

    private BrandResponse mapToResponse(Brand brand) {
        return new BrandResponse(
                brand.getId(),
                brand.getName(),
                brand.getDescription(),
                brand.getLogoUrl(),
                brand.getCreatedAt(),
                brand.getUpdatedAt());
    }
}
