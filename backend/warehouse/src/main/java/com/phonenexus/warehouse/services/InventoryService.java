package com.phonenexus.warehouse.services;

import java.util.UUID;

public interface InventoryService {
    void updateStock(UUID warehouseId, UUID variantId, Integer quantityChange);
}
