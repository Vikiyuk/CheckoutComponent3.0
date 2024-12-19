package com.example.checkout;

import com.example.checkout.controller.CheckoutController;
import com.example.checkout.model.Cart;
import com.example.checkout.model.Discount;
import com.example.checkout.model.Item;
import com.example.checkout.service.CheckoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CheckoutUnitTest {

    private MockMvc mockMvc;

    @Mock
    private CheckoutService checkoutService;

    @InjectMocks
    private CheckoutController checkoutController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(checkoutController).build();
    }
    @Test
    void testScanItemWithInvalidQuantityThrowsException() throws Exception {
        Cart cart = new Cart();
        List<Item> mockInventory = List.of(new Item("A", "Apple", 40, 30, 3));
        when(checkoutService.getInventory()).thenReturn(mockInventory);

        mockMvc.perform(post("/api/checkout/scan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\": \"A\", \"quantity\": -1}")
                        .sessionAttr("cart", cart))
                .andExpect(status().isBadRequest());

    }
    @Test
    void testScanItem() throws Exception {
        Cart cart = new Cart();

        List<Item> mockInventory = List.of(new Item("A", "Apple", 40, 30, 3));
        when(checkoutService.getInventory()).thenReturn(mockInventory);

        mockMvc.perform(post("/api/checkout/scan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\": \"A\", \"quantity\": 1}")
                        .sessionAttr("cart", cart))
                .andExpect(status().isOk());

        ArgumentCaptor<Item> itemCaptor = ArgumentCaptor.forClass(Item.class);
        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);

        verify(checkoutService).scanItem(itemCaptor.capture(), cartCaptor.capture(), eq(1));
        assertEquals("A", itemCaptor.getValue().getId());
        assertSame(cart, cartCaptor.getValue());
    }

    @Test
    void testGetTotal() throws Exception {
        when(checkoutService.calculateTotal(any(), any(), any())).thenReturn(100.0);

        mockMvc.perform(get("/api/checkout/total"))
                .andExpect(status().isOk())
                .andExpect(content().string("100.0"));

        verify(checkoutService, times(1)).calculateTotal(any(), any(), any());
    }

    @Test
    void testPayAndGenerateReceipt() throws Exception {
        Cart cart = new Cart();
        cart.getItems().put("A", 2);
        List<Item> mockInventory = List.of(new Item("A", "Apple", 40, 30, 3));
        List<Discount> mockDiscounts = List.of(new Discount("A", "B", 5));
        when(checkoutService.getInventory()).thenReturn(mockInventory);
        when(checkoutService.getDiscounts()).thenReturn(mockDiscounts);
        double expectedTotal = checkoutService.calculateTotal(mockInventory, mockDiscounts, cart);
        doNothing().when(checkoutService).resetCart(any(Cart.class));
        mockMvc.perform(post("/api/checkout/pay")
                        .sessionAttr("cart", cart))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Receipt:")))
                .andExpect(content().string(containsString("Apple x 2")))
                .andExpect(content().string(containsString("Total: " + expectedTotal)));
        verify(checkoutService, times(1)).resetCart(cart);
    }

    @Test
    void testResetCart() throws Exception {
        mockMvc.perform(put("/api/checkout/reset"))
                .andExpect(status().isOk());

        verify(checkoutService, times(1)).resetCart(any(Cart.class));
    }
}
