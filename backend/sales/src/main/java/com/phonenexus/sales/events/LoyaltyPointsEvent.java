package com.phonenexus.sales.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoyaltyPointsEvent {
    private String userId;
    private Integer pointsEarned;
    private BigDecimal amountSpent;
    private String orderId;
}
