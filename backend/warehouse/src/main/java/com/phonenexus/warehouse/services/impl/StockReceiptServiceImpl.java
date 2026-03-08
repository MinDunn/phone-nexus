package com.phonenexus.warehouse.services.impl;

import com.phonenexus.warehouse.exceptions.ResourceNotFoundException;
import com.phonenexus.warehouse.models.MovementType;
import com.phonenexus.warehouse.models.StockMovement;
import com.phonenexus.warehouse.models.StockReceipt;
import com.phonenexus.warehouse.payload.request.StockReceiptRequest;
import com.phonenexus.warehouse.payload.response.StockReceiptResponse;
import com.phonenexus.warehouse.repositories.StockMovementRepository;
import com.phonenexus.warehouse.repositories.StockReceiptRepository;
import com.phonenexus.warehouse.repositories.SupplierRepository;
import com.phonenexus.warehouse.repositories.WarehouseRepository;
import com.phonenexus.warehouse.services.InventoryService;
import com.phonenexus.warehouse.services.StockReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StockReceiptServiceImpl implements StockReceiptService {

    @Autowired
    private StockReceiptRepository receiptRepository;

    @Autowired
    private StockMovementRepository movementRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private InventoryService inventoryService;

    @Override
    @Transactional
    public StockReceiptResponse createReceipt(StockReceiptRequest request) {
        StockReceipt receipt = new StockReceipt();
        receipt.setReceiptNumber(request.getReceiptNumber());
        receipt.setReceiptDate(LocalDateTime.now());
        receipt.setSupplierName(request.getSupplierName());
        receipt.setNotes(request.getNote());

        if (request.getSupplierId() != null) {
            receipt.setSupplier(supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Supplier not found: " + request.getSupplierId())));
        }

        List<StockMovement> movements = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (StockReceiptRequest.StockReceiptItemRequest item : request.getItems()) {
            StockMovement movement = new StockMovement();
            movement.setVariantId(item.getVariantId());
            movement.setQuantity(item.getQuantity());
            movement.setCostPrice(item.getUnitCost());
            movement.setType(MovementType.IN);
            movement.setReason("PURCHASE: " + request.getReceiptNumber());
            movement.setReceipt(receipt);
            movement.setWarehouse(warehouseRepository.findById(request.getWarehouseId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Warehouse not found: " + request.getWarehouseId())));

            if (receipt.getSupplier() != null) {
                movement.setSupplier(receipt.getSupplier());
            }

            movements.add(movement);
            totalAmount = totalAmount.add(item.getUnitCost().multiply(new BigDecimal(item.getQuantity())));

            // Update Inventory
            inventoryService.updateStock(request.getWarehouseId(), item.getVariantId(), item.getQuantity());
        }

        receipt.setTotalAmount(totalAmount);
        receipt.setMovements(movements);

        StockReceipt saved = receiptRepository.save(receipt);
        return mapToResponse(saved);
    }

    @Override
    public StockReceiptResponse getReceiptById(UUID id) {
        StockReceipt receipt = receiptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found: " + id));
        return mapToResponse(receipt);
    }

    @Override
    public Page<StockReceiptResponse> getAllReceipts(Pageable pageable) {
        return receiptRepository.findAll(pageable).map(this::mapToResponse);
    }

    private StockReceiptResponse mapToResponse(StockReceipt receipt) {
        List<StockReceiptResponse.StockMovementResponse> movementDTOs = receipt.getMovements().stream()
                .map(m -> StockReceiptResponse.StockMovementResponse.builder()
                        .id(m.getId())
                        .variantId(m.getVariantId())
                        .quantity(m.getQuantity())
                        .unitPrice(m.getCostPrice())
                        .type(m.getType().name())
                        .build())
                .collect(Collectors.toList());

        return StockReceiptResponse.builder()
                .id(receipt.getId())
                .receiptNumber(receipt.getReceiptNumber())
                .receiptDate(receipt.getReceiptDate())
                .supplierName(receipt.getSupplierName())
                .supplierId(receipt.getSupplier() != null ? receipt.getSupplier().getId() : null)
                .totalAmount(receipt.getTotalAmount())
                .note(receipt.getNotes())
                .movements(movementDTOs)
                .build();
    }
}
