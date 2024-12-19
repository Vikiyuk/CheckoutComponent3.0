package com.example.checkout.model;

public class Item {
    private String id;
    private String name;
    private double price;
    private double bulkPrice;
    private int bulkQuantity;

    public Item(String id, String name, double price, double bulkPrice, int bulkQuantity) {
        if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("ID cannot be null or empty");
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Name cannot be null or empty");
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative");
        if (bulkPrice < 0) throw new IllegalArgumentException("Bulk price cannot be negative");
        if (bulkQuantity < 1) throw new IllegalArgumentException("Bulk quantity must be at least 1");

        this.id = id;
        this.name = name;
        this.price = price;
        this.bulkPrice = bulkPrice;
        this.bulkQuantity = bulkQuantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getBulkPrice() {
        return bulkPrice;
    }

    public void setBulkPrice(double bulkPrice) {
        this.bulkPrice = bulkPrice;
    }

    public int getBulkQuantity() {
        return bulkQuantity;
    }

    public void setBulkQuantity(int bulkQuantity) {
        this.bulkQuantity = bulkQuantity;
    }
}