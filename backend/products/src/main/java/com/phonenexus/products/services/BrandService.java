package com.phonenexus.products.services;

import com.phonenexus.products.payload.request.BrandRequest;
import com.phonenexus.products.payload.response.BrandResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BrandService {
    BrandResponse createBrand(BrandRequest request);

    BrandResponse updateBrand(UUID id, BrandRequest request);

    BrandResponse getBrandById(UUID id);

    Page<BrandResponse> getAllBrands(Pageable pageable);

    void deleteBrand(UUID id);
}
