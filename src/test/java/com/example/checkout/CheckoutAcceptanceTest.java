package com.example.checkout;

import com.example.checkout.controller.CheckoutController;
import com.example.checkout.model.Cart;
import com.example.checkout.model.Item;
import com.example.checkout.service.CheckoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class CheckoutAcceptanceTest {


    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    @Test
    void endToEndTest() throws Exception {
        Cart cart = new Cart();


        mockMvc.perform(post("/api/checkout/scan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\":\"A\",\"quantity\":\"4\"}").sessionAttr("cart", cart))

                .andExpect(status().isOk());

        mockMvc.perform(post("/api/checkout/scan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\": \"B\",\"quantity\":\"1\"}\n")
                .sessionAttr("cart", cart))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/checkout/total").sessionAttr("cart", cart))
                .andExpect(status().isOk())
                .andExpect(content().string("135.0"));

        mockMvc.perform(post("/api/checkout/pay").sessionAttr("cart", cart))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Receipt")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Apple x 4")));

        mockMvc.perform(get("/api/checkout/cart").sessionAttr("cart", cart))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"items\":{}}"));
    }
}