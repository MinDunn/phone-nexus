package com.phonenexus.identities.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoyaltyPointsEvent {
    private String userId;
    private Integer pointsEarned;
    private BigDecimal amountSpent;
    private String orderId;
}
