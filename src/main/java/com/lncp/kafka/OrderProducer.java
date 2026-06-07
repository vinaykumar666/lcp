package com.lncp.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderProducer {
    private static final String TOPIC_ORDER_CREATED = "order-created";
    private static final String TOPIC_ORDER_UPDATED = "order-status-updated";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public void publishOrderCreated(OrderEvent event) {
        publishEvent(TOPIC_ORDER_CREATED, event);
    }

    public void publishOrderStatusUpdated(OrderEvent event) {
        publishEvent(TOPIC_ORDER_UPDATED, event);
    }

    private void publishEvent(String topic, OrderEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, event.getOrderId(), payload);
            log.info("Published event [{}] for order {}", event.getEventType(), event.getOrderId());
        } catch (Exception e) {
            log.error("Failed to publish Kafka event for order {}: {}", event.getOrderId(), e.getMessage());
        }
    }
}
