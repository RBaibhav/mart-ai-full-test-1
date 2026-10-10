package com.shopsmart.orders.service;

import com.shopsmart.common.ApiException;
import com.shopsmart.common.JsonUtil;
import com.shopsmart.orders.dto.OrderDetailDto;
import com.shopsmart.orders.dto.OrderItemDetailDto;
import com.shopsmart.orders.dto.OrderSummaryDto;
import com.shopsmart.orders.dto.ShipmentEventDto;
import com.shopsmart.orders.model.Order;
import com.shopsmart.orders.model.OrderItem;
import com.shopsmart.orders.model.ShipmentEvent;
import com.shopsmart.orders.repository.OrderItemRepository;
import com.shopsmart.orders.repository.OrderRepository;
import com.shopsmart.orders.repository.ShipmentEventRepository;
import com.shopsmart.security.CurrentUser;
import com.shopsmart.security.UserContext;
import com.shopsmart.shopping.model.Product;
import com.shopsmart.shopping.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShipmentEventRepository shipmentEventRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        ShipmentEventRepository shipmentEventRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.shipmentEventRepository = shipmentEventRepository;
        this.productRepository = productRepository;
    }

    private UUID getAuthenticatedCustomerId() {
        CurrentUser user = UserContext.get();
        if (user == null) {
            throw new ApiException("UNAUTHORIZED", "Authentication required to access orders.", HttpStatus.UNAUTHORIZED);
        }
        return user.getId();
    }

    public List<OrderSummaryDto> getCustomerOrders() {
        UUID customerId = getAuthenticatedCustomerId();
        List<Order> orders = orderRepository.findByCustomerIdOrderByOrderDateDesc(customerId);
        return orders.stream().map(order -> {
            List<OrderItem> items = orderItemRepository.findByIdOrderId(order.getId());
            int itemCount = items.stream().mapToInt(OrderItem::getQuantity).sum();
            return new OrderSummaryDto(
                    order.getId(),
                    order.getCustomerId(),
                    order.getStatus(),
                    order.getTotalAmount(),
                    order.getOrderDate(),
                    itemCount
            );
        }).collect(Collectors.toList());
    }

    public OrderDetailDto getOrderById(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException("NOT_FOUND", "Order not found with ID: " + orderId, HttpStatus.NOT_FOUND));

        CurrentUser user = UserContext.get();
        // Check ownership safeguard: customer cannot view another customer's order unless admin
        if (user != null && !user.getEmail().equalsIgnoreCase("admin@shopsmart.com") && !order.getCustomerId().equals(user.getId())) {
            throw new ApiException("FORBIDDEN", "Access denied: you do not have permission to view this order.", HttpStatus.FORBIDDEN);
        }

        List<OrderItem> items = orderItemRepository.findByIdOrderId(order.getId());
        List<OrderItemDetailDto> itemDtos = new ArrayList<>();
        for (OrderItem item : items) {
            Optional<Product> prodOpt = productRepository.findById(item.getId().getProductId());
            String name = prodOpt.map(Product::getName).orElse("Unknown Product");
            String brand = prodOpt.map(Product::getBrand).orElse("Unknown");
            List<String> images = prodOpt.map(p -> JsonUtil.toList(p.getImages())).orElse(new ArrayList<>());
            BigDecimal subtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

            itemDtos.add(new OrderItemDetailDto(
                    item.getId().getProductId(),
                    name,
                    brand,
                    images,
                    item.getQuantity(),
                    item.getUnitPrice(),
                    subtotal
            ));
        }

        List<ShipmentEvent> shipmentEvents = shipmentEventRepository.findByOrderIdOrderByTimestampAsc(order.getId());
        List<ShipmentEventDto> eventDtos = shipmentEvents.stream().map(e -> new ShipmentEventDto(
                e.getId(),
                e.getOrderId(),
                e.getStatus(),
                e.getTimestamp(),
                e.getEstimatedDelivery()
        )).collect(Collectors.toList());

        return new OrderDetailDto(
                order.getId(),
                order.getCustomerId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getOrderDate(),
                itemDtos,
                eventDtos
        );
    }
}
