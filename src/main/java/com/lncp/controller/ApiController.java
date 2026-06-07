package com.lncp.controller;

import com.lncp.dto.OrderResponse;
import com.lncp.entity.Order;
import com.lncp.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class ApiController {

    private final OrderService orderService;

    @GetMapping("/orders/{orderId}/status")
    public ResponseEntity<OrderResponse> getOrderStatus(@PathVariable String orderId) {
        Order order = orderService.getByOrderId(orderId);
        return ResponseEntity.ok(orderService.toResponse(order));
    }
}
