package com.phonenexus.sales.repositories;

import com.phonenexus.sales.models.Cart;
import com.phonenexus.sales.models.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByUserIdAndStatus(String userId, CartStatus status);

    void deleteByStatusAndUpdatedAtBefore(CartStatus status, java.time.LocalDateTime dateTime);
}
