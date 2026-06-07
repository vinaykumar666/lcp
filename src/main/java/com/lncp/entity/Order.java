package com.lncp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", unique = true, nullable = false)
    private String orderId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.ACCEPTED;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum OrderStatus {
        ACCEPTED, PREPARING, READY, DELIVERED;

        public String getBadgeClass() {
            return switch (this) {
                case ACCEPTED -> "bg-primary";
                case PREPARING -> "bg-warning text-dark";
                case READY -> "bg-success";
                case DELIVERED -> "bg-secondary";
            };
        }
    }

    @PreUpdate
    public void preUpdate() { this.updatedAt = LocalDateTime.now(); }
}
