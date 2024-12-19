package com.example.checkout;

import com.example.checkout.model.Cart;
import com.example.checkout.model.Item;
import com.example.checkout.service.CheckoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CheckoutIntegrationTest {

    private CheckoutService checkoutService;
    private Cart cart;

    @BeforeEach
    void setUp() {
        checkoutService = new CheckoutService();
        cart = new Cart();
    }

    @Test
    void testScanItem() {
        Item item = new Item("A", "Apple", 40, 30, 3);
        checkoutService.scanItem(item, cart,2);
        assertTrue(cart.getItems().containsKey("A") && cart.getItems().get("A") == 2);;
    }

    @Test
    void testCalculateTotalWithBulkAndDiscounts() {
        cart.getItems().put("A", 3);
        cart.getItems().put("B", 2);

        double total = checkoutService.calculateTotal(checkoutService.getInventory(), checkoutService.getDiscounts(), cart);

        assertEquals(95.0, total);
    }

    @Test
    void testResetCart() {
        cart.getItems().put("A", 3);
        checkoutService.resetCart(cart);
        assertTrue(cart.getItems().isEmpty());
    }
}