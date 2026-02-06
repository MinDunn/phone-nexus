package com.phonenexus.sales.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesStatsResponse {
    private BigDecimal totalRevenue;
    private long totalOrders;
    private long pendingOrders;
    private long completedOrders;
    private long cancelledOrders;
    private BigDecimal totalCost;
    private BigDecimal netProfit;
    private java.util.Map<String, BigDecimal> monthlyProfit;
    private Map<String, Long> ordersByPaymentMethod;
}
