package com.phonenexus.identities.listeners;

import com.phonenexus.identities.config.RabbitMQConfig;
import com.phonenexus.identities.events.LoyaltyPointsEvent;
import com.phonenexus.identities.services.LoyaltyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LoyaltyEventListener {

    private static final Logger log = LoggerFactory.getLogger(LoyaltyEventListener.class);

    @Autowired
    private LoyaltyService loyaltyService;

    @RabbitListener(queues = RabbitMQConfig.LOYALTY_QUEUE)
    public void handleLoyaltyPointsEarned(LoyaltyPointsEvent event) {
        log.info("Received loyalty points event for user: {}, points: {}", event.getUserId(), event.getPointsEarned());
        try {
            loyaltyService.addPoints(
                    UUID.fromString(event.getUserId()),
                    event.getPointsEarned(),
                    event.getAmountSpent());
        } catch (Exception e) {
            log.error("Failed to process loyalty points event for user {}: {}", event.getUserId(), e.getMessage());
        }
    }
}
