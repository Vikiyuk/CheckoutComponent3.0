package com.example.checkout.controller;

import com.example.checkout.model.Cart;
import com.example.checkout.model.Discount;
import com.example.checkout.model.Item;
import com.example.checkout.request.ScanRequest;
import com.example.checkout.service.CheckoutService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/checkout")
@SessionAttributes("cart")
@Validated
public class CheckoutController {

    private static final String RECEIPT_HEADER = "Receipt:\n";
    private static final String NO_ITEMS_MESSAGE = "No items in the cart.\n";

    @Autowired
    private CheckoutService checkoutService;

    /**
        Cart is stored in session
    **/
    @ModelAttribute("cart")
    public Cart initializeCart() {
        return new Cart();
    }

    @PostMapping("/scan")
    public void addItemToCart(@Valid @RequestBody ScanRequest scanRequest, @ModelAttribute("cart") Cart cart) {
        try {
            Item item = findItemById(scanRequest.getItemId());
            checkoutService.scanItem(item, cart, scanRequest.getQuantity());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        }
    }

    @GetMapping("/total")
    public double calculateTotal(@ModelAttribute("cart") Cart cart) {
        return checkoutService.calculateTotal(checkoutService.getInventory(), checkoutService.getDiscounts(), cart);
    }

    @PostMapping("/pay")
    public String processPaymentAndGenerateReceipt(@ModelAttribute("cart") Cart cart) {
        double totalCost = calculateTotal(cart);

        StringBuilder receipt = new StringBuilder(RECEIPT_HEADER);
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            receipt.append(NO_ITEMS_MESSAGE)
                    .append("\nTotal: ").append(totalCost).append("\n");
            return receipt.toString();
        }

        receipt.append(buildItemsDetails(cart));
        String discountDetails = buildDiscountDetails(cart);
        if (!discountDetails.isEmpty()) {
            receipt.append("\nApplied Discounts:\n").append(discountDetails);
        }

        receipt.append("\nTotal: ").append(totalCost).append("\n");
        checkoutService.resetCart(cart);
        return receipt.toString();
    }

    @GetMapping("/cart")
    public Cart getCartContents(@ModelAttribute("cart") Cart cart) {
        return checkoutService.getCart(cart);
    }

    @PutMapping("/reset")
    public ResponseEntity<String> resetCart(@ModelAttribute("cart") Cart cart) {
        checkoutService.resetCart(cart);
        return ResponseEntity.ok("Cart has been reset successfully");
    }




    /** Utility methods */

    private Item findItemById(String itemId) {
        return checkoutService.getInventory().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item not found in inventory"));
    }

    private String buildItemsDetails(Cart cart) {
        StringBuilder itemDetails = new StringBuilder("\nPurchased Items:\n");
        cart.getItems().forEach((id, quantity) -> {
            Item item = findItemById(id);
            itemDetails.append(formatItemDetails(item, quantity));
        });
        return itemDetails.toString();
    }

    private String formatItemDetails(Item item, int quantity) {
        if (item.getBulkQuantity() > 0 && quantity >= item.getBulkQuantity()) {
            int bulkSets = quantity / item.getBulkQuantity();
            int remainder = quantity % item.getBulkQuantity();
            double bulkPrice = bulkSets * item.getBulkQuantity() * item.getBulkPrice();
            double remainderPrice = remainder * item.getPrice();
            return String.format("%s x %d (%d bulk at %.2f, %d at regular price) = %.2f\n",
                    item.getName(), quantity, bulkSets, item.getBulkPrice(), remainder, bulkPrice + remainderPrice);
        } else {
            return String.format("%s x %d = %.2f\n", item.getName(), quantity, quantity * item.getPrice());
        }
    }

    private String buildDiscountDetails(Cart cart) {
        StringBuilder discountDetails = new StringBuilder();
        for (Discount discount : checkoutService.getDiscounts()) {
            int countX = cart.getItems().getOrDefault(discount.getItemX(), 0);
            int countY = cart.getItems().getOrDefault(discount.getItemY(), 0);
            int applicableDiscounts = Math.min(countX, countY);
            if (applicableDiscounts > 0) {
                discountDetails.append(String.format("Buy %s with %s discount: -%.2f\n",
                        discount.getItemX(), discount.getItemY(), applicableDiscounts * discount.getSavings()));
            }
        }
        return discountDetails.toString();
    }
}