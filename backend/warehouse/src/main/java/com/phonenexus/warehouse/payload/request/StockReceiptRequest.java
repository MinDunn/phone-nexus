package com.phonenexus.warehouse.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class StockReceiptRequest {
    @NotBlank(message = "Receipt number is required")
    private String receiptNumber;

    @NotBlank(message = "Supplier name is required")
    private String supplierName;

    private UUID supplierId;

    private UUID warehouseId;

    @NotEmpty(message = "Items are required")
    private List<StockReceiptItemRequest> items;

    private String note;

    @Data
    public static class StockReceiptItemRequest {
        private UUID productId;
        private UUID variantId;
        private String productName;
        private String variantName;
        private String sku;
        private Integer quantity;
        private BigDecimal unitCost;
    }
}
