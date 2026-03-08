package com.phonenexus.warehouse.services;

import com.phonenexus.warehouse.payload.request.StockReceiptRequest;
import com.phonenexus.warehouse.payload.response.StockReceiptResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface StockReceiptService {
    StockReceiptResponse createReceipt(StockReceiptRequest request);

    StockReceiptResponse getReceiptById(UUID id);

    Page<StockReceiptResponse> getAllReceipts(Pageable pageable);
}
