package com.lncp.dto;

import com.lncp.entity.MenuItem;
import com.lncp.entity.Order;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderRequest {
    private String customerName;
    private String mobileNumber;
    private Map<Long, Integer> items; // menuItemId -> quantity
}

