package com.example.checkout.controller;

import com.example.checkout.model.Cart;
import com.example.checkout.model.Discount;
import com.example.checkout.model.Item;
import com.example.checkout.service.CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {
    List<Item> inventory = List.of(
            new Item("A", "Apple", 40, 30, 3),
            new Item("B", "Banana", 10, 7.5, 2)
    );

    List<Discount> discounts = List.of(
            new Discount("A", "B", 5)
    );
    @Autowired
    private CheckoutService checkoutService;

    /**
     * Adds an item to the virtual checkout cart by passing the item details.
     */
    @PostMapping("/scan")
    public void scanItem(@RequestBody Item item) {
        if (item == null || item.getId() == null || item.getName() == null) {
            throw new IllegalArgumentException("Invalid item input");
        }
        checkoutService.scanItem(item);
    }

    /**
     * Calculates the total cost of the items in the cart, applying any relevant discounts.
     * returns the total cost after applying discounts.
     */
    @GetMapping("/total")
    public double getTotal() {
        try {
            return checkoutService.calculateTotal(inventory, discounts);
        } catch (Exception e) {
            throw new RuntimeException("Error calculating total: " + e.getMessage());
        }
    }

    /**
     * Processes customer payment and generates a detailed receipt, which includes
     * the purchased items, any discounts applied, and the total cost. After the receipt is generated, the cart is reset for the next transaction.
     * returns A string representation of the receipt detailing the transaction, purchased items, applied discounts, and total cost.
     */
    @PostMapping("/pay")
    public String payAndGenerateReceipt() {
        double total = checkoutService.calculateTotal(inventory, discounts);
        Cart cart = checkoutService.getCart();

        StringBuilder receipt = new StringBuilder("Receipt:\n");

        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            receipt.append("No items in the cart.\n");
            receipt.append("\nTotal: ").append(total).append("\n");
            return receipt.toString();
        }

        receipt.append("\nPurchased Items:\n");
        cart.getItems().forEach((id, quantity) -> {
            Item item = inventory.stream().filter(i -> i.getId().equals(id)).findFirst().orElse(null);
            if (item != null) {
                if (item.getBulkQuantity() > 0 && quantity >= item.getBulkQuantity()) {
                    int bulkSets = quantity / item.getBulkQuantity();
                    int remainder = quantity % item.getBulkQuantity();
                    double bulkPrice = bulkSets * item.getBulkQuantity() * item.getBulkPrice();
                    double remainderPrice = remainder * item.getPrice();
                    receipt.append(item.getName())
                            .append(" x ")
                            .append(quantity)
                            .append(" (").append(bulkSets).append(" bulk at ").append(item.getBulkPrice()).append(", ").append(remainder).append(" at regular price)")
                            .append(" = ").append(bulkPrice + remainderPrice)
                            .append("\n");
                } else {
                    receipt.append(item.getName())
                            .append(" x ")
                            .append(quantity)
                            .append(" = ")
                            .append(quantity * item.getPrice())
                            .append("\n");
                }
            } else {
                receipt.append("Unknown Item ID: ").append(id).append("\n");
            }
        });

        boolean discountsApplied = false;
        StringBuilder discountDetails = new StringBuilder("\nApplied Discounts:\n");
        for (Discount discount : discounts) {
            int countX = cart.getItems().getOrDefault(discount.getItemX(), 0);
            int countY = cart.getItems().getOrDefault(discount.getItemY(), 0);
            int applicableDiscounts = Math.min(countX, countY);
            if (applicableDiscounts > 0) {
                discountsApplied = true;
                discountDetails.append("Buy ").append(discount.getItemX()).append(" with ").append(discount.getItemY())
                        .append(" discount: -").append(applicableDiscounts * discount.getSavings()).append("\n");
            }
        }

        if (discountsApplied) {
            receipt.append(discountDetails);
        }

        receipt.append("\nTotal: ").append(total).append("\n");
        checkoutService.resetCart();
        return receipt.toString();
    }
    @GetMapping("/cart")
    public Cart getCartContents() {
        return checkoutService.getCart();
    }
    @PostMapping("/reset")
    public void resetCart() {
        checkoutService.resetCart();
    }
}