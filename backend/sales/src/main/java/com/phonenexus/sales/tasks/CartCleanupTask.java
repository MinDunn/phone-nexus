package com.phonenexus.sales.tasks;

import com.phonenexus.sales.models.CartStatus;
import com.phonenexus.sales.repositories.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class CartCleanupTask {

    @Autowired
    private CartRepository cartRepository;

    /**
     * Clean up ACTIVE carts that haven't been updated for 30 days.
     * Runs every day at 1 AM.
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void cleanupOldCarts() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
        cartRepository.deleteByStatusAndUpdatedAtBefore(CartStatus.ACTIVE, threshold);
        // Add logging if needed
    }
}
