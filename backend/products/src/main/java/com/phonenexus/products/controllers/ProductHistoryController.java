package com.phonenexus.products.controllers;

import com.phonenexus.products.models.ProductHistory;
import com.phonenexus.products.payload.response.ProductHistoryResponse;
import com.phonenexus.products.repositories.ProductHistoryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/products/history")
@Tag(name = "Product History", description = "APIs for viewing price and stock fluctuation history")
public class ProductHistoryController {

    @Autowired
    private ProductHistoryRepository historyRepository;

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get history by Product ID")
    public ResponseEntity<List<ProductHistoryResponse>> getHistoryByProduct(@PathVariable UUID productId) {
        List<ProductHistory> historyList = historyRepository.findByProductIdOrderByChangedAtDesc(productId);
        return ResponseEntity.ok(historyList.stream().map(this::mapToResponse).collect(Collectors.toList()));
    }

    @GetMapping("/variant/{variantId}")
    @Operation(summary = "Get history by Variant ID")
    public ResponseEntity<List<ProductHistoryResponse>> getHistoryByVariant(@PathVariable UUID variantId) {
        List<ProductHistory> historyList = historyRepository.findByVariantIdOrderByChangedAtDesc(variantId);
        return ResponseEntity.ok(historyList.stream().map(this::mapToResponse).collect(Collectors.toList()));
    }

    private ProductHistoryResponse mapToResponse(ProductHistory history) {
        return new ProductHistoryResponse(
                history.getId(),
                history.getProductId(),
                history.getVariantId(),
                history.getSku(),
                history.getOldPrice(),
                history.getNewPrice(),
                history.getOldStock(),
                history.getNewStock(),
                history.getActionType(),
                history.getChangedAt(),
                history.getChangedBy());
    }
}
