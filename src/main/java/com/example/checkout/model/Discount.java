package com.example.checkout.model;

public class Discount {
    private String itemX;
    private String itemY;
    private double savings;

    public Discount(String itemX, String itemY, double savings) {
        this.itemX = itemX;
        this.itemY = itemY;
        this.savings = savings;
    }

    public String getItemX() {
        return itemX;
    }

    public void setItemX(String itemX) {
        this.itemX = itemX;
    }

    public String getItemY() {
        return itemY;
    }

    public void setItemY(String itemY) {
        this.itemY = itemY;
    }

    public double getSavings() {
        return savings;
    }

    public void setSavings(double savings) {
        this.savings = savings;
    }
}

