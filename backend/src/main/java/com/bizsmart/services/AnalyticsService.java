package com.bizsmart.services;

import com.bizsmart.models.Order;
import com.bizsmart.models.Product;
import com.bizsmart.payload.response.DashboardSummaryResponse;
import com.bizsmart.repositories.CustomerRepository;
import com.bizsmart.repositories.DemandForecastRepository;
import com.bizsmart.repositories.OrderRepository;
import com.bizsmart.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DemandForecastRepository demandForecastRepository;

    public DashboardSummaryResponse getDashboardSummary() {
        BigDecimal totalRevenue = orderRepository.calculateTotalRevenue();
        long totalProducts = productRepository.count();
        List<Product> lowStockProducts = productRepository.findLowStockProducts();
        long totalCustomers = customerRepository.count();
        long totalOrders = orderRepository.count();
        long pendingOrders = orderRepository.countPendingOrders();

        List<Order> recentOrdersList = orderRepository.findAllByOrderByCreatedAtDesc()
                .stream().limit(5).collect(Collectors.toList());

        List<Map<String, Object>> recentOrders = recentOrdersList.stream().map(o -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", o.getId());
            map.put("orderNumber", o.getOrderNumber());
            map.put("customerName", o.getCustomer().getName());
            map.put("totalAmount", o.getTotalAmount());
            map.put("status", o.getStatus().name());
            map.put("createdAt", o.getCreatedAt().toString());
            return map;
        }).collect(Collectors.toList());

        List<Map<String, Object>> lowStockAlerts = lowStockProducts.stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getId());
            map.put("sku", p.getSku());
            map.put("name", p.getName());
            map.put("stockQuantity", p.getStockQuantity());
            map.put("safetyStock", p.getSafetyStock());
            map.put("reorderQuantity", p.getReorderQuantity());
            return map;
        }).collect(Collectors.toList());

        // Sample demand trend points for the dashboard chart
        List<Map<String, Object>> demandTrends = List.of(
                Map.of("date", "Mon", "actual", 42, "predicted", 45),
                Map.of("date", "Tue", "actual", 50, "predicted", 48),
                Map.of("date", "Wed", "actual", 65, "predicted", 62),
                Map.of("date", "Thu", "actual", 58, "predicted", 60),
                Map.of("date", "Fri", "actual", 82, "predicted", 80),
                Map.of("date", "Sat", "actual", 95, "predicted", 90),
                Map.of("date", "Sun", "actual", 70, "predicted", 75)
        );

        return DashboardSummaryResponse.builder()
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .totalProducts(totalProducts)
                .lowStockCount(lowStockProducts.size())
                .totalCustomers(totalCustomers)
                .totalOrders(totalOrders)
                .pendingOrders(pendingOrders)
                .recentOrders(recentOrders)
                .lowStockAlerts(lowStockAlerts)
                .demandTrends(demandTrends)
                .build();
    }
}
