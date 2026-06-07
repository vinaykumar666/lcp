package com.lncp.service;

import com.lncp.dto.MenuItemDto;
import com.lncp.entity.MenuItem;
import com.lncp.exception.ResourceNotFoundException;
import com.lncp.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuItemRepository menuItemRepository;

    public List<MenuItem> getAllAvailableItems() {
        return menuItemRepository.findByAvailableTrueOrderByCategory();
    }

    public Map<String, List<MenuItem>> getMenuGroupedByCategory() {
        List<MenuItem> items = menuItemRepository.findByAvailableTrueOrderByCategory();
        Map<String, List<MenuItem>> grouped = new LinkedHashMap<>();

        List<MenuItem> specials = items.stream().filter(MenuItem::isTodaySpecial).collect(Collectors.toList());
        if (!specials.isEmpty()) grouped.put("Today's Specials", specials);

        for (MenuItem.Category cat : new MenuItem.Category[]{
            MenuItem.Category.VEG_CURRIES, MenuItem.Category.NON_VEG_CURRIES, MenuItem.Category.RICE_ITEMS}) {
            List<MenuItem> catItems = items.stream()
                .filter(i -> i.getCategory() == cat)
                .collect(Collectors.toList());
            if (!catItems.isEmpty()) grouped.put(cat.name().replace("_", " "), catItems);
        }
        return grouped;
    }

    public List<MenuItem> getAllItems() {
        return menuItemRepository.findAll();
    }

    public MenuItem getById(Long id) {
        return menuItemRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Menu item not found: " + id));
    }

    @Transactional
    public MenuItem save(MenuItemDto dto) {
        MenuItem item = MenuItem.builder()
            .name(dto.getName())
            .description(dto.getDescription())
            .price(dto.getPrice())
            .category(dto.getCategory())
            .imageUrl(dto.getImageUrl())
            .available(dto.isAvailable())
            .todaySpecial(dto.isTodaySpecial())
            .build();
        MenuItem saved = menuItemRepository.save(item);
        log.info("Menu item created: {} ({})", saved.getName(), saved.getId());
        return saved;
    }

    @Transactional
    public MenuItem update(Long id, MenuItemDto dto) {
        MenuItem item = getById(id);
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setCategory(dto.getCategory());
        item.setImageUrl(dto.getImageUrl());
        item.setAvailable(dto.isAvailable());
        item.setTodaySpecial(dto.isTodaySpecial());
        MenuItem saved = menuItemRepository.save(item);
        log.info("Menu item updated: {} ({})", saved.getName(), saved.getId());
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        menuItemRepository.deleteById(id);
        log.info("Menu item deleted: {}", id);
    }

    @Transactional
    public void toggleAvailability(Long id) {
        MenuItem item = getById(id);
        item.setAvailable(!item.isAvailable());
        menuItemRepository.save(item);
        log.info("Menu item {} availability toggled to {}", id, item.isAvailable());
    }

    @Transactional
    public void toggleTodaySpecial(Long id) {
        MenuItem item = getById(id);
        item.setTodaySpecial(!item.isTodaySpecial());
        menuItemRepository.save(item);
    }
}
