package com.phonenexus.products.controllers;

import com.phonenexus.products.payload.request.BrandRequest;
import com.phonenexus.products.payload.response.BrandResponse;
import com.phonenexus.products.services.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products/brands")
@Tag(name = "Brand Management", description = "APIs for managing product brands")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @PostMapping
    @Operation(summary = "Create a new brand")
    public ResponseEntity<BrandResponse> createBrand(@Valid @RequestBody BrandRequest request) {
        return ResponseEntity.ok(brandService.createBrand(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing brand")
    public ResponseEntity<BrandResponse> updateBrand(@PathVariable UUID id, @Valid @RequestBody BrandRequest request) {
        return ResponseEntity.ok(brandService.updateBrand(id, request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get brand by ID")
    public ResponseEntity<BrandResponse> getBrandById(@PathVariable UUID id) {
        return ResponseEntity.ok(brandService.getBrandById(id));
    }

    @GetMapping
    @Operation(summary = "Get all brands with pagination")
    public ResponseEntity<Page<BrandResponse>> getAllBrands(Pageable pageable) {
        return ResponseEntity.ok(brandService.getAllBrands(pageable));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a brand")
    public ResponseEntity<Void> deleteBrand(@PathVariable UUID id) {
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }
}
