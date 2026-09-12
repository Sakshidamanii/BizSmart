package com.bizsmart.payload.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
public class DashboardSummaryResponse {
    private BigDecimal totalRevenue;
    private long totalProducts;
    private long lowStockCount;
    private long totalCustomers;
    private long totalOrders;
    private long pendingOrders;
    private List<Map<String, Object>> recentOrders;
    private List<Map<String, Object>> lowStockAlerts;
    private List<Map<String, Object>> demandTrends;
}
