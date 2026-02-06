package com.phonenexus.sales.payload.response;

import java.math.BigDecimal;
import java.util.Map;

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

    public SalesStatsResponse() {
    }

    public SalesStatsResponse(BigDecimal totalRevenue, long totalOrders, long pendingOrders,
            long completedOrders, long cancelledOrders, BigDecimal totalCost,
            BigDecimal netProfit, java.util.Map<String, BigDecimal> monthlyProfit,
            Map<String, Long> ordersByPaymentMethod) {
        this.totalRevenue = totalRevenue;
        this.totalOrders = totalOrders;
        this.pendingOrders = pendingOrders;
        this.completedOrders = completedOrders;
        this.cancelledOrders = cancelledOrders;
        this.totalCost = totalCost;
        this.netProfit = netProfit;
        this.monthlyProfit = monthlyProfit;
        this.ordersByPaymentMethod = ordersByPaymentMethod;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public long getPendingOrders() {
        return pendingOrders;
    }

    public void setPendingOrders(long pendingOrders) {
        this.pendingOrders = pendingOrders;
    }

    public long getCompletedOrders() {
        return completedOrders;
    }

    public void setCompletedOrders(long completedOrders) {
        this.completedOrders = completedOrders;
    }

    public long getCancelledOrders() {
        return cancelledOrders;
    }

    public void setCancelledOrders(long cancelledOrders) {
        this.cancelledOrders = cancelledOrders;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public BigDecimal getNetProfit() {
        return netProfit;
    }

    public void setNetProfit(BigDecimal netProfit) {
        this.netProfit = netProfit;
    }

    public java.util.Map<String, BigDecimal> getMonthlyProfit() {
        return monthlyProfit;
    }

    public void setMonthlyProfit(java.util.Map<String, BigDecimal> monthlyProfit) {
        this.monthlyProfit = monthlyProfit;
    }

    public Map<String, Long> getOrdersByPaymentMethod() {
        return ordersByPaymentMethod;
    }

    public void setOrdersByPaymentMethod(Map<String, Long> ordersByPaymentMethod) {
        this.ordersByPaymentMethod = ordersByPaymentMethod;
    }

    public static SalesStatsResponseBuilder builder() {
        return new SalesStatsResponseBuilder();
    }

    public static class SalesStatsResponseBuilder {
        private BigDecimal totalRevenue;
        private long totalOrders;
        private long pendingOrders;
        private long completedOrders;
        private long cancelledOrders;
        private BigDecimal totalCost;
        private BigDecimal netProfit;
        private java.util.Map<String, BigDecimal> monthlyProfit;
        private Map<String, Long> ordersByPaymentMethod;

        SalesStatsResponseBuilder() {
        }

        public SalesStatsResponseBuilder totalRevenue(BigDecimal totalRevenue) {
            this.totalRevenue = totalRevenue;
            return this;
        }

        public SalesStatsResponseBuilder totalOrders(long totalOrders) {
            this.totalOrders = totalOrders;
            return this;
        }

        public SalesStatsResponseBuilder pendingOrders(long pendingOrders) {
            this.pendingOrders = pendingOrders;
            return this;
        }

        public SalesStatsResponseBuilder completedOrders(long completedOrders) {
            this.completedOrders = completedOrders;
            return this;
        }

        public SalesStatsResponseBuilder cancelledOrders(long cancelledOrders) {
            this.cancelledOrders = cancelledOrders;
            return this;
        }

        public SalesStatsResponseBuilder totalCost(BigDecimal totalCost) {
            this.totalCost = totalCost;
            return this;
        }

        public SalesStatsResponseBuilder netProfit(BigDecimal netProfit) {
            this.netProfit = netProfit;
            return this;
        }

        public SalesStatsResponseBuilder monthlyProfit(java.util.Map<String, BigDecimal> monthlyProfit) {
            this.monthlyProfit = monthlyProfit;
            return this;
        }

        public SalesStatsResponseBuilder ordersByPaymentMethod(Map<String, Long> ordersByPaymentMethod) {
            this.ordersByPaymentMethod = ordersByPaymentMethod;
            return this;
        }

        public SalesStatsResponse build() {
            return new SalesStatsResponse(totalRevenue, totalOrders, pendingOrders, completedOrders, cancelledOrders,
                    totalCost, netProfit, monthlyProfit, ordersByPaymentMethod);
        }
    }
}
