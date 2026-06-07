package com.lncp.service;

import com.lncp.dto.OrderRequest;
import com.lncp.dto.OrderResponse;
import com.lncp.entity.*;
import com.lncp.exception.ResourceNotFoundException;
import com.lncp.kafka.OrderEvent;
import com.lncp.kafka.OrderProducer;
import com.lncp.repository.MenuItemRepository;
import com.lncp.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderProducer orderProducer;

    @Value("${app.order.prefix:LNCP}")
    private String orderPrefix;

    @Transactional
    public Order placeOrder(OrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }

        String orderId = generateOrderId();
        BigDecimal total = BigDecimal.ZERO;

        Order order = Order.builder()
            .orderId(orderId)
            .customerName(request.getCustomerName())
            .mobileNumber(request.getMobileNumber())
            .status(Order.OrderStatus.ACCEPTED)
            .totalAmount(BigDecimal.ZERO)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        for (Map.Entry<Long, Integer> entry : request.getItems().entrySet()) {
            MenuItem menuItem = menuItemRepository.findById(entry.getKey())
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found: " + entry.getKey()));
            int qty = entry.getValue();
            BigDecimal subtotal = menuItem.getPrice().multiply(BigDecimal.valueOf(qty));
            total = total.add(subtotal);

            OrderItem oi = OrderItem.builder()
                .order(order)
                .menuItem(menuItem)
                .quantity(qty)
                .unitPrice(menuItem.getPrice())
                .subtotal(subtotal)
                .build();
            order.getOrderItems().add(oi);
        }

        order.setTotalAmount(total);
        Order saved = orderRepository.save(order);
        log.info("Order placed: {} | Total: {}", orderId, total);

        publishOrderCreatedEvent(saved);
        return saved;
    }

    public Order getByOrderId(String orderId) {
        return orderRepository.findByOrderId(orderId.toUpperCase())
            .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Order> searchOrders(String query) {
        return orderRepository.findByOrderIdContainingIgnoreCaseOrderByCreatedAtDesc(query);
    }

    @Transactional
    public Order updateStatus(String orderId, Order.OrderStatus newStatus) {
        Order order = getByOrderId(orderId);
        Order.OrderStatus oldStatus = order.getStatus();
        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        Order saved = orderRepository.save(order);
        log.info("Order {} status changed: {} -> {}", orderId, oldStatus, newStatus);

        publishStatusUpdatedEvent(saved);
        return saved;
    }

    public OrderResponse toResponse(Order order) {
        List<OrderResponse.OrderItemResponse> items = order.getOrderItems().stream()
            .map(oi -> OrderResponse.OrderItemResponse.builder()
                .itemName(oi.getMenuItem().getName())
                .quantity(oi.getQuantity())
                .unitPrice(oi.getUnitPrice())
                .subtotal(oi.getSubtotal())
                .build())
            .collect(Collectors.toList());

        return OrderResponse.builder()
            .id(order.getId())
            .orderId(order.getOrderId())
            .customerName(order.getCustomerName())
            .mobileNumber(order.getMobileNumber())
            .status(order.getStatus())
            .statusBadgeClass(order.getStatus().getBadgeClass())
            .totalAmount(order.getTotalAmount())
            .orderItems(items)
            .createdAt(order.getCreatedAt())
            .updatedAt(order.getUpdatedAt())
            .build();
    }

    private synchronized String generateOrderId() {
        Integer max = orderRepository.findMaxOrderSequence();
        int next = (max == null ? 1000 : max) + 1;
        return orderPrefix + "-" + next;
    }

    private void publishOrderCreatedEvent(Order order) {
        try {
            orderProducer.publishOrderCreated(OrderEvent.builder()
                .eventType("ORDER_CREATED")
                .orderId(order.getOrderId())
                .customerName(order.getCustomerName())
                .mobileNumber(order.getMobileNumber())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount().doubleValue())
                .timestamp(LocalDateTime.now())
                .build());
        } catch (Exception e) {
            log.warn("Kafka publish failed (non-critical): {}", e.getMessage());
        }
    }

    private void publishStatusUpdatedEvent(Order order) {
        try {
            orderProducer.publishOrderStatusUpdated(OrderEvent.builder()
                .eventType("ORDER_STATUS_UPDATED")
                .orderId(order.getOrderId())
                .status(order.getStatus().name())
                .timestamp(LocalDateTime.now())
                .build());
        } catch (Exception e) {
            log.warn("Kafka publish failed (non-critical): {}", e.getMessage());
        }
    }
}
