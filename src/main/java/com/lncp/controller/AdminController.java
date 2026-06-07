package com.lncp.controller;

import com.lncp.dto.MenuItemDto;
import com.lncp.entity.MenuItem;
import com.lncp.entity.Order;
import com.lncp.service.MenuService;
import com.lncp.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MenuService menuService;
    private final OrderService orderService;

    @GetMapping("/login")
    public String loginPage() { return "admin/login"; }

    // ── Dashboard ──────────────────────────────────────────────────────────
    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model, @RequestParam(required = false) String search) {
        var orders = (search != null && !search.isBlank())
            ? orderService.searchOrders(search)
            : orderService.getAllOrders();
        model.addAttribute("orders", orders);
        model.addAttribute("search", search);
        model.addAttribute("totalOrders", orders.size());
        model.addAttribute("pendingOrders", orders.stream()
            .filter(o -> o.getStatus() != Order.OrderStatus.DELIVERED).count());
        return "admin/dashboard";
    }

    // ── Order Management ───────────────────────────────────────────────────
    @GetMapping("/orders/{orderId}")
    public String orderDetail(@PathVariable String orderId, Model model) {
        Order order = orderService.getByOrderId(orderId);
        model.addAttribute("order", orderService.toResponse(order));
        model.addAttribute("statuses", Order.OrderStatus.values());
        return "admin/order-detail";
    }

    @PostMapping("/orders/{orderId}/status")
    public String updateStatus(@PathVariable String orderId,
                               @RequestParam Order.OrderStatus status,
                               RedirectAttributes ra) {
        orderService.updateStatus(orderId, status);
        ra.addFlashAttribute("success", "Order " + orderId + " status updated to " + status);
        return "redirect:/admin/orders/" + orderId;
    }

    // ── Menu Management ────────────────────────────────────────────────────
    @GetMapping("/menu")
    public String menuList(Model model) {
        model.addAttribute("items", menuService.getAllItems());
        return "admin/menu-list";
    }

    @GetMapping("/menu/new")
    public String newItemForm(Model model) {
        model.addAttribute("menuItem", new MenuItemDto());
        model.addAttribute("categories", MenuItem.Category.values());
        return "admin/menu-form";
    }

    @PostMapping("/menu/new")
    public String createItem(@Valid @ModelAttribute("menuItem") MenuItemDto dto,
                              BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("categories", MenuItem.Category.values());
            return "admin/menu-form";
        }
        menuService.save(dto);
        ra.addFlashAttribute("success", "Menu item created: " + dto.getName());
        return "redirect:/admin/menu";
    }

    @GetMapping("/menu/{id}/edit")
    public String editItemForm(@PathVariable Long id, Model model) {
        MenuItem item = menuService.getById(id);
        MenuItemDto dto = MenuItemDto.builder()
            .id(item.getId()).name(item.getName()).description(item.getDescription())
            .price(item.getPrice()).category(item.getCategory()).imageUrl(item.getImageUrl())
            .available(item.isAvailable()).todaySpecial(item.isTodaySpecial()).build();
        model.addAttribute("menuItem", dto);
        model.addAttribute("categories", MenuItem.Category.values());
        return "admin/menu-form";
    }

    @PostMapping("/menu/{id}/edit")
    public String updateItem(@PathVariable Long id,
                              @Valid @ModelAttribute("menuItem") MenuItemDto dto,
                              BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("categories", MenuItem.Category.values());
            return "admin/menu-form";
        }
        menuService.update(id, dto);
        ra.addFlashAttribute("success", "Menu item updated: " + dto.getName());
        return "redirect:/admin/menu";
    }

    @PostMapping("/menu/{id}/delete")
    public String deleteItem(@PathVariable Long id, RedirectAttributes ra) {
        MenuItem item = menuService.getById(id);
        menuService.delete(id);
        ra.addFlashAttribute("success", "Menu item deleted: " + item.getName());
        return "redirect:/admin/menu";
    }

    @PostMapping("/menu/{id}/toggle-availability")
    public String toggleAvailability(@PathVariable Long id, RedirectAttributes ra) {
        menuService.toggleAvailability(id);
        ra.addFlashAttribute("success", "Availability toggled");
        return "redirect:/admin/menu";
    }

    @PostMapping("/menu/{id}/toggle-special")
    public String toggleSpecial(@PathVariable Long id, RedirectAttributes ra) {
        menuService.toggleTodaySpecial(id);
        ra.addFlashAttribute("success", "Today's Special toggled");
        return "redirect:/admin/menu";
    }
}
