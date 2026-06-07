package com.lncp.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderConsumer {
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @KafkaListener(topics = "order-created", groupId = "lncp-group")
    public void consumeOrderCreated(String message) {
        try {
            OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
            log.info("[KAFKA] ORDER_CREATED - OrderId: {}, Customer: {}, Amount: {}",
                    event.getOrderId(), event.getCustomerName(), event.getTotalAmount());
        } catch (Exception e) {
            log.error("[KAFKA] Failed to process order-created message: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "order-status-updated", groupId = "lncp-group")
    public void consumeOrderStatusUpdated(String message) {
        try {
            OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
            log.info("[KAFKA] ORDER_STATUS_UPDATED - OrderId: {}, Status: {}",
                    event.getOrderId(), event.getStatus());
        } catch (Exception e) {
            log.error("[KAFKA] Failed to process order-status-updated message: {}", e.getMessage());
        }
    }
}
