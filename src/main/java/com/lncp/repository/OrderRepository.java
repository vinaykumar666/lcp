package com.lncp.repository;

import com.lncp.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderId(String orderId);
    List<Order> findAllByOrderByCreatedAtDesc();
    List<Order> findByOrderIdContainingIgnoreCaseOrderByCreatedAtDesc(String orderId);

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(o.orderId, 6) AS int)), 1000) FROM Order o")
    Integer findMaxOrderSequence();
}
