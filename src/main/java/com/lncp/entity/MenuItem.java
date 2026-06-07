package com.lncp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "menu_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MenuItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_available")
    private boolean available = true;

    @Column(name = "is_today_special")
    private boolean todaySpecial = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Category {
        TODAYS_SPECIALS, VEG_CURRIES, NON_VEG_CURRIES, RICE_ITEMS
    }

    public String getCategoryDisplayName() {
        return switch (category) {
            case TODAYS_SPECIALS -> "Today's Specials";
            case VEG_CURRIES -> "Veg Curries";
            case NON_VEG_CURRIES -> "Non-Veg Curries";
            case RICE_ITEMS -> "Rice Items";
        };
    }
}
