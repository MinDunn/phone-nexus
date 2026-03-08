package com.phonenexus.warehouse.repositories;

import com.phonenexus.warehouse.models.Inventory;
import com.phonenexus.warehouse.models.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    Optional<Inventory> findByVariantIdAndWarehouse(UUID variantId, Warehouse warehouse);

    Optional<Inventory> findByVariantIdAndWarehouseId(UUID variantId, UUID warehouseId);

    List<Inventory> findByVariantId(UUID variantId);

    List<Inventory> findByWarehouse(Warehouse warehouse);
}
