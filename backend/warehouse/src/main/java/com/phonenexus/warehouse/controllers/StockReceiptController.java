package com.phonenexus.warehouse.controllers;

import com.phonenexus.warehouse.payload.request.StockReceiptRequest;
import com.phonenexus.warehouse.payload.response.ApiResponse;
import com.phonenexus.warehouse.payload.response.StockReceiptResponse;
import com.phonenexus.warehouse.services.StockReceiptService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/warehouse/receipts")
public class StockReceiptController {

    @Autowired
    private StockReceiptService receiptService;

    @PostMapping
    public ResponseEntity<ApiResponse<StockReceiptResponse>> createReceipt(
            @Valid @RequestBody StockReceiptRequest request) {
        return ResponseEntity
                .ok(ApiResponse.success("Stock receipt created successfully", receiptService.createReceipt(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StockReceiptResponse>> getReceiptById(@PathVariable UUID id) {
        return ResponseEntity
                .ok(ApiResponse.success("Stock receipt fetched successfully", receiptService.getReceiptById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<StockReceiptResponse>>> getAllReceipts(Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.success("Stock receipts fetched successfully", receiptService.getAllReceipts(pageable)));
    }
}
