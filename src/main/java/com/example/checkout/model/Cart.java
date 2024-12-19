package com.example.checkout.model;

import java.util.HashMap;
import java.util.Map;
public class Cart {
    private Map<String, Integer> items = new HashMap<>();
    
    public Map<String, Integer> getItems() {
        return items;
    }
    
    public void setItems(Map<String, Integer> items) {
        this.items = items;
    }
    public void reset() {
        items.clear();
    }
}
