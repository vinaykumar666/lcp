package com.lncp.kafka;

import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderEvent {
    private String eventType;
    private String orderId;
    private String customerName;
    private String mobileNumber;
    private String status;
    private double totalAmount;
    private LocalDateTime timestamp;
}
