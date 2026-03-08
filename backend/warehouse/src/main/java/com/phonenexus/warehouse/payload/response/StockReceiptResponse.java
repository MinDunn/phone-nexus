package com.phonenexus.warehouse.payload.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class StockReceiptResponse {
    private UUID id;
    private String receiptNumber;
    private LocalDateTime receiptDate;
    private String supplierName;
    private UUID supplierId;
    private BigDecimal totalAmount;
    private String note;
    private List<StockMovementResponse> movements;

    @Data
    @Builder
    public static class StockMovementResponse {
        private UUID id;
        private UUID productId;
        private UUID variantId;
        private String sku;
        private Integer quantity;
        private BigDecimal unitPrice;
        private String type;
    }
}
