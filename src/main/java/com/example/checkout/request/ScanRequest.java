package com.example.checkout.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class ScanRequest {
    @NotBlank(message = "Item ID cannot be blank")
    private String itemId;

    @Positive(message = "Quantity must be greater than zero")
    private int quantity;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
