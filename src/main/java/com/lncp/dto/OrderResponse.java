package com.lncp.dto;

import com.lncp.entity.Order;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderResponse {
    private Long id;
    private String orderId;
    private String customerName;
    private String mobileNumber;
    private Order.OrderStatus status;
    private String statusBadgeClass;
    private BigDecimal totalAmount;
    private List<OrderItemResponse> orderItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class OrderItemResponse {
        private String itemName;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
    }
}
