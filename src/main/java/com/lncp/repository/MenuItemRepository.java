package com.lncp.repository;

import com.lncp.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByAvailableTrueOrderByCategory();
    List<MenuItem> findByCategoryAndAvailableTrue(MenuItem.Category category);
    List<MenuItem> findByTodaySpecialTrueAndAvailableTrue();
    List<MenuItem> findByNameContainingIgnoreCase(String name);
}
