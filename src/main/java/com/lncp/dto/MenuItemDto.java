package com.lncp.dto;

import com.lncp.entity.MenuItem;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MenuItemDto {
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be positive")
    private BigDecimal price;

    @NotNull(message = "Category is required")
    private MenuItem.Category category;

    private String imageUrl;
    private boolean available = true;
    private boolean todaySpecial = false;
}
