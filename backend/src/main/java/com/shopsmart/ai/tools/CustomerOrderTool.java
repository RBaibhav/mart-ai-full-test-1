package com.shopsmart.ai.tools;

import com.shopsmart.orders.dto.OrderDetailDto;
import com.shopsmart.orders.dto.OrderSummaryDto;
import com.shopsmart.orders.service.OrderService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class CustomerOrderTool {

    private final OrderService orderService;

    public CustomerOrderTool(OrderService orderService) {
        this.orderService = orderService;
    }

    public List<OrderSummaryDto> getCustomerOrders() {
        return orderService.getCustomerOrders();
    }

    public OrderDetailDto getOrderDetail(UUID orderId) {
        return orderService.getOrderById(orderId);
    }
}
