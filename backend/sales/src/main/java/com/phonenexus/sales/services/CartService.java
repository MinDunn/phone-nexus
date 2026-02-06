package com.phonenexus.sales.services;

import com.phonenexus.sales.payload.request.AddToCartRequest;
import com.phonenexus.sales.payload.response.CartResponse;

import java.util.UUID;

public interface CartService {
    CartResponse addToCart(String userId, AddToCartRequest request);

    CartResponse getCart(String userId);

    CartResponse removeFromCart(String userId, UUID cartItemId);

    void clearCart(String userId);
}
