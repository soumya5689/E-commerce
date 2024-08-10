package com.aryan.ecom.services.admin.adminOrder;

import com.aryan.ecom.dto.AnalyticsResponse;
import com.aryan.ecom.dto.OrderDto;

import java.util.List;

public interface AdminOrderService {
    List<OrderDto> getAllPlacedOrders();

    OrderDto changeOrderStatus(Long orderId, String status);

    AnalyticsResponse calculateAnalytics();

    Long getTotalOrdersForMonths(int month, int year);

    Long getTotalEarningsForMonth(int month, int year);
}
