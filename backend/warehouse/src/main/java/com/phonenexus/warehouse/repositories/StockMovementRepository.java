package com.phonenexus.warehouse.repositories;

import com.phonenexus.warehouse.models.StockMovement;
import com.phonenexus.warehouse.models.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {
    List<StockMovement> findByVariantId(UUID variantId);

    List<StockMovement> findByWarehouse(Warehouse warehouse);
}
