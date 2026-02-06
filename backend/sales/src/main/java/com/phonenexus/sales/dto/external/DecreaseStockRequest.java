package com.phonenexus.sales.dto.external;

public class DecreaseStockRequest {
    private Integer quantity;

    public DecreaseStockRequest() {
    }

    public DecreaseStockRequest(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
