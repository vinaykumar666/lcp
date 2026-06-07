package com.lncp.config;

import com.lncp.entity.MenuItem;
import com.lncp.entity.User;
import com.lncp.repository.MenuItemRepository;
import com.lncp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MenuItemRepository menuItemRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        loadUsers();
        loadMenuItems();
    }

    private void loadUsers() {
        if (userRepository.count() == 0) {
            userRepository.save(User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .role("ADMIN")
                .active(true)
                .build());
            log.info("Default admin user created: admin/admin123");
        }
    }

    private void loadMenuItems() {
        if (menuItemRepository.count() == 0) {
            // Today's Specials
            save("Mutton Biryani Special", "Fragrant basmati rice with tender mutton pieces", 180, MenuItem.Category.TODAYS_SPECIALS, true, true);
            save("Chicken Chettinad Curry", "Spicy Chettinad style chicken curry", 160, MenuItem.Category.TODAYS_SPECIALS, true, true);
            save("Pesarattu with Upma", "Green moong dosa served with upma", 80, MenuItem.Category.TODAYS_SPECIALS, true, true);

            // Veg Curries
            save("Dal Tadka", "Yellow lentils tempered with ghee and spices", 70, MenuItem.Category.VEG_CURRIES, false, true);
            save("Palak Paneer", "Spinach gravy with paneer cubes", 110, MenuItem.Category.VEG_CURRIES, false, true);
            save("Baingan Bharta", "Smoky roasted eggplant curry", 90, MenuItem.Category.VEG_CURRIES, false, true);
            save("Mixed Vegetable Curry", "Seasonal vegetables in spiced gravy", 80, MenuItem.Category.VEG_CURRIES, false, true);
            save("Aloo Gobi", "Potato and cauliflower dry curry", 85, MenuItem.Category.VEG_CURRIES, false, true);
            save("Sambar", "Traditional South Indian lentil curry", 60, MenuItem.Category.VEG_CURRIES, false, true);

            // Non-Veg Curries
            save("Chicken Curry", "Classic South Indian chicken curry", 140, MenuItem.Category.NON_VEG_CURRIES, false, true);
            save("Mutton Curry", "Slow-cooked tender mutton curry", 170, MenuItem.Category.NON_VEG_CURRIES, false, true);
            save("Egg Curry", "Boiled eggs in spiced onion-tomato gravy", 90, MenuItem.Category.NON_VEG_CURRIES, false, true);
            save("Fish Curry", "Fresh fish in tangy tamarind curry", 150, MenuItem.Category.NON_VEG_CURRIES, false, true);
            save("Prawn Masala", "Juicy prawns in spiced masala gravy", 180, MenuItem.Category.NON_VEG_CURRIES, false, true);
            save("Chicken Fry", "Crispy fried chicken with spices", 160, MenuItem.Category.NON_VEG_CURRIES, false, false);

            // Rice Items
            save("Steamed Rice", "Plain steamed basmati rice", 40, MenuItem.Category.RICE_ITEMS, false, true);
            save("Veg Biryani", "Aromatic vegetable biryani", 120, MenuItem.Category.RICE_ITEMS, false, true);
            save("Chicken Biryani", "Flavorful chicken biryani", 160, MenuItem.Category.RICE_ITEMS, false, true);
            save("Mutton Biryani", "Rich mutton biryani", 180, MenuItem.Category.RICE_ITEMS, false, true);
            save("Curd Rice", "Cooling curd rice with tadka", 60, MenuItem.Category.RICE_ITEMS, false, true);
            save("Lemon Rice", "Tangy lemon rice with peanuts", 70, MenuItem.Category.RICE_ITEMS, false, true);

            log.info("Sample menu items loaded: {} items", menuItemRepository.count());
        }
    }

    private void save(String name, String desc, int price, MenuItem.Category cat, boolean special, boolean available) {
        menuItemRepository.save(MenuItem.builder()
            .name(name)
            .description(desc)
            .price(BigDecimal.valueOf(price))
            .category(cat)
            .todaySpecial(special)
            .available(available)
            .imageUrl(getImageEmoji(cat))
            .build());
    }

    private String getImageEmoji(MenuItem.Category cat) {
        return switch (cat) {
            case TODAYS_SPECIALS -> "/images/special.svg";
            case VEG_CURRIES -> "/images/veg.svg";
            case NON_VEG_CURRIES -> "/images/nonveg.svg";
            case RICE_ITEMS -> "/images/rice.svg";
        };
    }
}
