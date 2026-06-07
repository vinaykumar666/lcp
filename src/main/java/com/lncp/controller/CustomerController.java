package com.lncp.controller;

import com.lncp.dto.OrderRequest;
import com.lncp.entity.MenuItem;
import com.lncp.entity.Order;
import com.lncp.service.MenuService;
import com.lncp.service.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CustomerController {

    private final MenuService menuService;
    private final OrderService orderService;

    // ── Menu Page ──────────────────────────────────────────────────────────
    @GetMapping({"/", "/menu"})
    public String menu(Model model, HttpSession session) {
        model.addAttribute("menuGroups", menuService.getMenuGroupedByCategory());
        model.addAttribute("cart", getCart(session));
        model.addAttribute("cartCount", getCartCount(session));
        return "customer/menu";
    }

    // ── Cart Operations ────────────────────────────────────────────────────
    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long itemId, @RequestParam(defaultValue = "1") int quantity,
                            HttpSession session, RedirectAttributes ra) {
        MenuItem item = menuService.getById(itemId);
        Map<Long, Integer> cart = getCart(session);
        cart.merge(itemId, quantity, Integer::sum);
        session.setAttribute("cart", cart);
        session.setAttribute("cartItem_" + itemId, item.getName());
        ra.addFlashAttribute("success", item.getName() + " added to cart!");
        return "redirect:/menu";
    }

    @PostMapping("/cart/update")
    public String updateCart(@RequestParam Long itemId, @RequestParam int quantity, HttpSession session) {
        Map<Long, Integer> cart = getCart(session);
        if (quantity <= 0) cart.remove(itemId);
        else cart.put(itemId, quantity);
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam Long itemId, HttpSession session) {
        getCart(session).remove(itemId);
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session) {
        Map<Long, Integer> cart = getCart(session);
        List<CartItem> cartItems = buildCartItems(cart);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartCount", getCartCount(session));
        model.addAttribute("total", cartItems.stream()
            .mapToDouble(ci -> ci.getSubtotal()).sum());
        return "customer/cart";
    }

    // ── Order Placement ────────────────────────────────────────────────────
    @PostMapping("/order/place")
    public String placeOrder(@RequestParam(required = false) String customerName,
                             @RequestParam(required = false) String mobileNumber,
                             HttpSession session, RedirectAttributes ra) {
        Map<Long, Integer> cart = getCart(session);
        if (cart.isEmpty()) {
            ra.addFlashAttribute("error", "Your cart is empty!");
            return "redirect:/cart";
        }

        OrderRequest req = OrderRequest.builder()
            .customerName(customerName)
            .mobileNumber(mobileNumber)
            .items(cart)
            .build();

        Order order = orderService.placeOrder(req);
        session.removeAttribute("cart");
        log.info("Order placed by customer: {}", order.getOrderId());
        return "redirect:/order/success/" + order.getOrderId();
    }

    @GetMapping("/order/success/{orderId}")
    public String orderSuccess(@PathVariable String orderId, Model model) {
        Order order = orderService.getByOrderId(orderId);
        model.addAttribute("order", orderService.toResponse(order));
        return "customer/order-success";
    }

    // ── Order Tracking ─────────────────────────────────────────────────────
    @GetMapping("/track")
    public String trackPage() {
        return "customer/track";
    }

    @GetMapping("/track/{orderId}")
    public String trackOrder(@PathVariable String orderId, Model model) {
        try {
            Order order = orderService.getByOrderId(orderId);
            model.addAttribute("order", orderService.toResponse(order));
            model.addAttribute("orderId", orderId);
        } catch (Exception e) {
            model.addAttribute("error", "Order not found: " + orderId);
            model.addAttribute("orderId", orderId);
        }
        return "customer/track";
    }

    @PostMapping("/track")
    public String trackOrderPost(@RequestParam String orderId) {
        return "redirect:/track/" + orderId.trim().toUpperCase();
    }

    // ── Helpers ────────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private Map<Long, Integer> getCart(HttpSession session) {
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null) { cart = new LinkedHashMap<>(); session.setAttribute("cart", cart); }
        return cart;
    }

    private int getCartCount(HttpSession session) {
        return getCart(session).values().stream().mapToInt(Integer::intValue).sum();
    }

    private List<CartItem> buildCartItems(Map<Long, Integer> cart) {
        List<CartItem> result = new ArrayList<>();
        for (Map.Entry<Long, Integer> e : cart.entrySet()) {
            try {
                MenuItem item = menuService.getById(e.getKey());
                double sub = item.getPrice().doubleValue() * e.getValue();
                result.add(new CartItem(e.getKey(), item.getName(), item.getPrice().doubleValue(), e.getValue(), sub));
            } catch (Exception ex) { /* skip deleted items */ }
        }
        return result;
    }

    // Inner class for cart display
    public record CartItem(Long itemId, String name, double price, int quantity, double subtotal) {}
}
