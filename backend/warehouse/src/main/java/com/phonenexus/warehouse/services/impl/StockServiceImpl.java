package com.phonenexus.warehouse.services.impl;

import com.phonenexus.warehouse.exceptions.ResourceNotFoundException;
import com.phonenexus.warehouse.models.Inventory;
import com.phonenexus.warehouse.repositories.InventoryRepository;
import com.phonenexus.warehouse.repositories.WarehouseRepository;
import com.phonenexus.warehouse.services.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class StockServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Override
    @Transactional
    public void updateStock(UUID warehouseId, UUID variantId, Integer quantityChange) {
        Inventory inventory = inventoryRepository.findByVariantIdAndWarehouseId(variantId, warehouseId)
                .orElseGet(() -> {
                    Inventory newInv = new Inventory();
                    newInv.setWarehouse(warehouseRepository.findById(warehouseId)
                            .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found: " + warehouseId)));
                    newInv.setVariantId(variantId);
                    newInv.setQuantity(0);
                    newInv.setReorderLevel(10); // Default
                    return newInv;
                });

        inventory.setQuantity(inventory.getQuantity() + quantityChange);
        inventoryRepository.save(inventory);
    }
}
