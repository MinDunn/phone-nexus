package com.phonenexus.sales.services.impl;

import com.phonenexus.sales.clients.ProductClient;
import com.phonenexus.sales.dto.external.ProductExternalResponse;
import com.phonenexus.sales.dto.external.ProductVariantExternalResponse;
import com.phonenexus.sales.exception.ResourceNotFoundException;
import com.phonenexus.sales.models.Cart;
import com.phonenexus.sales.models.CartItem;
import com.phonenexus.sales.models.CartStatus;
import com.phonenexus.sales.payload.request.AddToCartRequest;
import com.phonenexus.sales.payload.response.CartItemResponse;
import com.phonenexus.sales.payload.response.CartResponse;
import com.phonenexus.sales.repositories.CartRepository;
import com.phonenexus.sales.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

        @Autowired
        private CartRepository cartRepository;

        @Autowired
        private ProductClient productClient;

        @Override
        @Transactional
        public CartResponse addToCart(String userId, AddToCartRequest request) {
                Cart cart = cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                                .orElseGet(() -> cartRepository.save(Cart.builder().userId(userId).build()));

                ProductExternalResponse product = productClient.getProductById(request.getProductId());
                if (product == null) {
                        throw new ResourceNotFoundException("Product not found with id: " + request.getProductId());
                }
                ProductVariantExternalResponse variant = product.getVariants().stream()
                                .filter(v -> v.getId().equals(request.getVariantId()))
                                .findFirst()
                                .orElseThrow(() -> new ResourceNotFoundException("Error: Product variant not found."));

                // Stock check moved to Order checkout or separate inventory service call
                /*
                 * if (variant.getStockQuantity() < request.getQuantity()) {
                 * throw new RuntimeException("Error: Not enough stock.");
                 * }
                 */

                Optional<CartItem> existingItem = cart.getItems().stream()
                                .filter(item -> item.getVariantId().equals(request.getVariantId()))
                                .findFirst();

                if (existingItem.isPresent()) {
                        CartItem item = existingItem.get();
                        item.setQuantity(item.getQuantity() + request.getQuantity());
                        // Update price in case it changed
                        item.setPrice(variant.getPrice());
                } else {
                        CartItem newItem = CartItem.builder()
                                        .cart(cart)
                                        .productId(request.getProductId())
                                        .variantId(request.getVariantId())
                                        .productName(product.getName())
                                        .price(variant.getPrice())
                                        .imageUrl(variant.getImageUrl())
                                        .quantity(request.getQuantity())
                                        .build();
                        cart.addItem(newItem);
                }

                updateTotalPrice(cart);
                cartRepository.save(cart);

                return mapToResponse(cart);
        }

        @Override
        public CartResponse getCart(String userId) {
                Cart cart = cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                                .orElseGet(() -> cartRepository.save(Cart.builder().userId(userId).build()));
                return mapToResponse(cart);
        }

        @Override
        @Transactional
        public CartResponse removeFromCart(String userId, UUID cartItemId) {
                Cart cart = cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                                .orElseThrow(() -> new RuntimeException("Error: Active cart not found."));

                CartItem itemToRemove = cart.getItems().stream()
                                .filter(item -> item.getId().equals(cartItemId))
                                .findFirst()
                                .orElseThrow(() -> new RuntimeException("Error: Cart item not found."));

                cart.removeItem(itemToRemove);
                updateTotalPrice(cart);
                cartRepository.save(cart);

                return mapToResponse(cart);
        }

        @Override
        @Transactional
        public void clearCart(String userId) {
                cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                                .ifPresent(cart -> {
                                        cart.getItems().clear();
                                        cart.setTotalPrice(BigDecimal.ZERO);
                                        cartRepository.save(cart);
                                });
        }

        private void updateTotalPrice(Cart cart) {
                BigDecimal total = cart.getItems().stream()
                                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                cart.setTotalPrice(total);
        }

        private CartResponse mapToResponse(Cart cart) {
                List<CartItemResponse> items = cart.getItems().stream()
                                .map(item -> new CartItemResponse(
                                                item.getId(),
                                                item.getProductId(),
                                                item.getProductName(),
                                                item.getQuantity(),
                                                item.getPrice(),
                                                item.getImageUrl()))
                                .collect(Collectors.toList());

                return new CartResponse(
                                cart.getId(),
                                cart.getUserId(),
                                items,
                                cart.getTotalPrice(),
                                cart.getStatus());
        }
}
