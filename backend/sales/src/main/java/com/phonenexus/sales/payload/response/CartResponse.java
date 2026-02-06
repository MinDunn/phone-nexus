package com.phonenexus.sales.payload.response;

import com.phonenexus.sales.models.CartStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class CartResponse {
    private UUID id;
    private String userId;
    private List<CartItemResponse> items;
    private BigDecimal totalPrice;
    private CartStatus status;

    public CartResponse(UUID id, String userId, List<CartItemResponse> items, BigDecimal totalPrice,
            CartStatus status) {
        this.id = id;
        this.userId = userId;
        this.items = items;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public List<CartItemResponse> getItems() {
        return items;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public CartStatus getStatus() {
        return status;
    }
}
